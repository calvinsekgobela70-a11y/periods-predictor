package com.example.data

import com.example.data.api.GeminiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LogRepository(private val logDao: LogDao) {
    val allLogs: Flow<List<DailyLog>> = logDao.getAllLogs()
    val settingsFlow: Flow<CycleSettings?> = logDao.getSettingsFlow()

    suspend fun getLogByDate(date: String): DailyLog? {
        return logDao.getLogByDate(date)
    }

    suspend fun insertLog(log: DailyLog) {
        logDao.insertLog(log)
    }

    suspend fun deleteLog(log: DailyLog) {
        logDao.deleteLog(log)
    }

    suspend fun getSettings(): CycleSettings {
        var settings = logDao.getSettings()
        if (settings == null) {
            settings = CycleSettings()
            logDao.insertSettings(settings)
        }
        return settings
    }

    suspend fun updateSettings(settings: CycleSettings) {
        logDao.insertSettings(settings)
    }

    suspend fun generateHealthInsightForRecent(symptoms: List<String>, moods: List<String>, flow: String?): String {
        // Log query or fetch latest data
        val insight = GeminiClient.generateHealthInsight(symptoms, moods, flow)
        
        // Save to settings so it persists
        val currentSettings = getSettings()
        val updatedSettings = currentSettings.copy(
            lastGeneratedInsight = insight,
            lastGeneratedInsightTime = System.currentTimeMillis()
        )
        logDao.insertSettings(updatedSettings)
        return insight
    }
}
