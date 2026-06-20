package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.api.GeminiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

sealed interface AiInsightState {
    object Idle : AiInsightState
    object Loading : AiInsightState
    data class Success(val insight: String) : AiInsightState
    data class Error(val message: String) : AiInsightState
}

class AuraViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LogDatabase.getDatabase(application)
    private val repository = LogRepository(database.logDao())

    val allLogs: StateFlow<List<DailyLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<CycleSettings> = repository.settingsFlow
        .map { it ?: CycleSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CycleSettings())

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _aiInsightState = MutableStateFlow<AiInsightState>(AiInsightState.Idle)
    val aiInsightState: StateFlow<AiInsightState> = _aiInsightState.asStateFlow()

    // Expose a flow for the log of the currently selected date
    val selectedDateLog: StateFlow<DailyLog?> = combine(selectedDate, allLogs) { date, logs ->
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        logs.find { it.date == dateStr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Run a dummy setup to ensure settings row is initialized
        viewModelScope.launch {
            repository.getSettings()
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        // Clear AI transition states when browsing
        if (_aiInsightState.value is AiInsightState.Error) {
            _aiInsightState.value = AiInsightState.Idle
        }
    }

    fun updateCycleSettings(typicalCycle: Int, typicalPeriod: Int, name: String) = viewModelScope.launch {
        val current = repository.getSettings()
        repository.updateSettings(
            current.copy(
                typicalCycleLength = typicalCycle,
                typicalPeriodLength = typicalPeriod,
                userName = name
            )
        )
    }

    fun togglePeriodForSelectedDate() = viewModelScope.launch {
        val dateStr = selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val currentLog = repository.getLogByDate(dateStr)
        if (currentLog == null) {
            val newLog = DailyLog(date = dateStr, isPeriod = true)
            repository.insertLog(newLog)
        } else {
            val updatedLog = currentLog.copy(isPeriod = !currentLog.isPeriod)
            repository.insertLog(updatedLog)
        }
    }

    fun updateSelectedDateLog(
        isPeriod: Boolean,
        flow: String,
        symptoms: List<String>,
        mood: String,
        notes: String
    ) = viewModelScope.launch {
        val dateStr = selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val symptomsStr = symptoms.joinToString(",")
        val updated = DailyLog(
            date = dateStr,
            isPeriod = isPeriod,
            flow = flow,
            symptoms = symptomsStr,
            mood = mood,
            notes = notes
        )
        repository.insertLog(updated)
    }

    fun toggleSymptomForSelectedDate(symptom: String) = viewModelScope.launch {
        val dateStr = selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val currentLog = repository.getLogByDate(dateStr) ?: DailyLog(date = dateStr)
        val list = if (currentLog.symptoms.isEmpty()) {
            mutableListOf()
        } else {
            currentLog.symptoms.split(",").toMutableList()
        }

        if (list.contains(symptom)) {
            list.remove(symptom)
        } else {
            list.add(symptom)
        }

        repository.insertLog(currentLog.copy(symptoms = list.joinToString(",")))
    }

    fun updateMoodForSelectedDate(mood: String) = viewModelScope.launch {
        val dateStr = selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val currentLog = repository.getLogByDate(dateStr) ?: DailyLog(date = dateStr)
        repository.insertLog(currentLog.copy(mood = mood))
    }

    fun updateNotesForSelectedDate(notes: String) = viewModelScope.launch {
        val dateStr = selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val currentLog = repository.getLogByDate(dateStr) ?: DailyLog(date = dateStr)
        repository.insertLog(currentLog.copy(notes = notes))
    }

    // Trigger AI Insight based on currently selected symptoms / moods or general cycle states
    fun fetchAiInsight() = viewModelScope.launch {
        _aiInsightState.value = AiInsightState.Loading
        try {
            // Compile recent symptoms and moods
            val logs = allLogs.value
            val recentLogs = logs.takeLast(7) // Check last 7 log entries
            val symptoms = recentLogs.flatMap { 
                if (it.symptoms.isEmpty()) emptyList() else it.symptoms.split(",") 
            }.distinct()
            
            val moods = recentLogs.map { it.mood }.filter { it != "Neutral" }.distinct()
            val hasFlow = recentLogs.firstOrNull { it.isPeriod }?.flow

            val insight = repository.generateHealthInsightForRecent(symptoms, moods, hasFlow)
            _aiInsightState.value = AiInsightState.Success(insight)
        } catch (e: Exception) {
            _aiInsightState.value = AiInsightState.Error(e.message ?: "Could not connect to Aura server")
        }
    }

    // --- CYCLES CALCULATION ENGINE ---

    // Expose calculated metrics
    val cyclePeriodBlocks: StateFlow<List<PeriodSegment>> = allLogs.map { logs ->
        groupConsecutivePeriodDays(logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calculatedCycleLength: StateFlow<Int> = combine(cyclePeriodBlocks, settings) { segments, currentSettings ->
        if (segments.size < 2) {
            currentSettings.typicalCycleLength
        } else {
            var totalDays = 0
            var count = 0
            for (i in 0 until segments.size - 1) {
                val currentStart = segments[i].startDate
                val nextStart = segments[i + 1].startDate
                val diffDays = ChronoUnit.DAYS.between(currentStart, nextStart).toInt()
                if (diffDays in 15..45) { // reasonable biological bounds
                    totalDays += diffDays
                    count++
                }
            }
            if (count > 0) totalDays / count else currentSettings.typicalCycleLength
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 28)

    // Last documented period start date (could be logged or is defaulted)
    val lastPeriodStartDate: StateFlow<LocalDate?> = cyclePeriodBlocks.map { segments ->
        segments.lastOrNull()?.startDate
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Predicted Next Period Date
    val predictedNextPeriodDate: StateFlow<LocalDate?> = combine(lastPeriodStartDate, calculatedCycleLength) { lastStart, cycleLength ->
        lastStart?.plusDays(cycleLength.toLong()) ?: LocalDate.now().plusDays(10) // fallback predicted date
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Days countdown to next period
    val daysToNextPeriod: StateFlow<Int> = predictedNextPeriodDate.map { expiry ->
        expiry?.let {
            ChronoUnit.DAYS.between(LocalDate.now(), it).toInt()
        } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Calculates status for a given date
    fun getCycleStateForDate(date: LocalDate): CycleState {
        val lastStart = lastPeriodStartDate.value
        val cycleLength = calculatedCycleLength.value
        val periodLength = settings.value.typicalPeriodLength

        if (lastStart == null) {
            return CycleState(
                phase = CyclePhase.FOLLICULAR,
                dayOfCycle = 1,
                phaseDescription = "Ready to start fresh. Tap on today's calendar and press 'Log Bleeding' to begin mapping your personal patterns!",
                isFertile = false
            )
        }

        val daysBetween = ChronoUnit.DAYS.between(lastStart, date)
        
        // Project forward or backward relative to last start
        val cycleOffset = if (daysBetween >= 0) {
            (daysBetween % cycleLength).toInt()
        } else {
            // handle pass dates elegantly
            val cycleOffsetNegative = (-daysBetween) % cycleLength
            if (cycleOffsetNegative == 0L) 0 else (cycleLength - cycleOffsetNegative).toInt()
        }

        val dayOfCycle = cycleOffset + 1
        val ovulationDay = cycleLength - 14
        val fertileStart = ovulationDay - 5
        val fertileEnd = ovulationDay + 1

        val isFertile = dayOfCycle in fertileStart..fertileEnd

        return when {
            dayOfCycle <= periodLength -> {
                CycleState(
                    phase = CyclePhase.MENSTRUAL,
                    dayOfCycle = dayOfCycle,
                    phaseDescription = "Your uterine lining is shedding. Energy and iron may draw low, so treat yourself to absolute warmth, slow activities, and cozy rests.",
                    isFertile = false
                )
            }
            dayOfCycle == ovulationDay -> {
                CycleState(
                    phase = CyclePhase.OVULATION,
                    dayOfCycle = dayOfCycle,
                    phaseDescription = "A mature egg is released. Your high energy, estrogen, and natural glow have hit their radiant peak. Express your creative heart!",
                    isFertile = true
                )
            }
            isFertile -> {
                CycleState(
                    phase = CyclePhase.FERTILE,
                    dayOfCycle = dayOfCycle,
                    phaseDescription = "Your body enters its fertile window. Higher moisture, bright social vibrations, and steady strength define this follicular spring phase.",
                    isFertile = true
                )
            }
            dayOfCycle > ovulationDay && dayOfCycle >= cycleLength - 4 -> {
                CycleState(
                    phase = CyclePhase.LUTEAL,
                    dayOfCycle = dayOfCycle,
                    phaseDescription = "PMS / pre-menstrual hormones might make you feel sensitive or slightly anxious. Turn inward with calming teas, cozy blankets, and warm boundaries.",
                    isFertile = false
                )
            }
            dayOfCycle < ovulationDay -> {
                CycleState(
                    phase = CyclePhase.FOLLICULAR,
                    dayOfCycle = dayOfCycle,
                    phaseDescription = "Estrogen starts its ascent, restoring vital strength and cell energy. A wonderful time to set intentions, plan projects, and glow forward.",
                    isFertile = false
                )
            }
            else -> {
                CycleState(
                    phase = CyclePhase.LUTEAL,
                    dayOfCycle = dayOfCycle,
                    phaseDescription = "Progesterone rises to slow down your engine. Listen to your intuition, prioritize slow evenings, and enjoy nourishing healthy foods.",
                    isFertile = false
                )
            }
        }
    }

    private fun groupConsecutivePeriodDays(logs: List<DailyLog>): List<PeriodSegment> {
        val periodLogsSorted = logs.filter { it.isPeriod }
            .map { LocalDate.parse(it.date) }
            .sorted()

        if (periodLogsSorted.isEmpty()) return emptyList()

        val segments = mutableListOf<PeriodSegment>()
        var currentStart = periodLogsSorted.first()
        var lastDate = currentStart

        for (i in 1 until periodLogsSorted.size) {
            val date = periodLogsSorted[i]
            // If the distance is more than 3 days, we treat it as a new separate period!
            if (ChronoUnit.DAYS.between(lastDate, date) > 3) {
                segments.add(PeriodSegment(startDate = currentStart, endDate = lastDate))
                currentStart = date
            }
            lastDate = date
        }
        segments.add(PeriodSegment(startDate = currentStart, endDate = lastDate))
        return segments
    }
}

data class PeriodSegment(
    val startDate: LocalDate,
    val endDate: LocalDate
)

enum class CyclePhase {
    MENSTRUAL,
    FOLLICULAR,
    FERTILE,
    OVULATION,
    LUTEAL
}

data class CycleState(
    val phase: CyclePhase,
    val dayOfCycle: Int,
    val phaseDescription: String,
    val isFertile: Boolean
)
