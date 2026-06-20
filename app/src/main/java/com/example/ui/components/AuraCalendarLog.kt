package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyLog
import com.example.viewmodel.AuraViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun AuraCalendarLog(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val selectedLog by viewModel.selectedDateLog.collectAsState()

    val currentLogSafe = selectedLog ?: DailyLog(date = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))

    // Track state values locally and sync to database
    val isPeriod = currentLogSafe.isPeriod
    val flow = currentLogSafe.flow
    val selectedSymptoms = if (currentLogSafe.symptoms.isEmpty()) emptyList() else currentLogSafe.symptoms.split(",")
    val selectedMood = currentLogSafe.mood
    val notesText = currentLogSafe.notes

    // Pre-create some symptoms and cute pastel colors
    val symptomsList = listOf(
        SymptomItem("Cramps", "⚡"),
        SymptomItem("Backache", "🦵"),
        SymptomItem("Headache", "🧠"),
        SymptomItem("Bloating", "🎈"),
        SymptomItem("Fatigue", "💤"),
        SymptomItem("Mood Swings", "🎭"),
        SymptomItem("Acne", "✨"),
        SymptomItem("Tender Breasts", "🎀")
    )

    val moodsList = listOf(
        MoodItem("Calm", "😌", Color(0xFFD3EAE1)),
        MoodItem("Happy", "😊", Color(0xFFFEE440)),
        MoodItem("Energetic", "🔥", Color(0xFFFFB703)),
        MoodItem("Neutral", "😐", Color(0xFFE5E5E5)),
        MoodItem("Sensitive", "🥺", Color(0xFFFFCAD4)),
        MoodItem("Sad", "😢", Color(0xFFD8E2DC)),
        MoodItem("Anxious", "😰", Color(0xFFD6E2E9))
    )

    // Generate date range of 14 days (7 before, 7 after today)
    val dates = remember {
        val today = LocalDate.now()
        (-7..7).map { today.plusDays(it.toLong()) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Topic Header
        Column(modifier = Modifier.padding(bottom = 4.dp)) {
            Text(
                text = "SELF-CARE JOURNAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF472B6),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "My Personal Cycle Logs",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D283E)
            )
        }

        // Horizontal Dates Slider
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(dates) { date ->
                val isSelected = date == selectedDate
                val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val dayLog = allLogs.find { it.date == dateStr }
                val hasPeriodTracked = dayLog?.isPeriod == true

                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = tween(200),
                    label = "Log Calendar Day Scale"
                )

                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(84.dp)
                        .shadow(if (isSelected) 6.dp else 1.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) {
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFCE7F3), Color(0xFFFFE4E6))
                                )
                            } else {
                                Brush.verticalGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.2f))
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFFFF7096) else Color.White.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.selectDate(date) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color(0xFFFF5277) else Color.Gray
                        )

                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D283E)
                        )

                        // Cute period small indicator
                        if (hasPeriodTracked) {
                            Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = "Active period on day",
                                tint = Color(0xFFFF4D6D),
                                modifier = Modifier.size(10.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Showing Currently Selected Date Detail
        Text(
            text = "LOGGING FOR: ${selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")).uppercase()}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7D788E),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )

        // 1. Log Period Switcher Row
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFF1F2), CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WaterDrop,
                                contentDescription = null,
                                tint = Color(0xFFFF5277),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Period active on this day?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D283E)
                            )
                            Text(
                                text = "Toggle custom bleeding logs",
                                fontSize = 11.sp,
                                color = Color(0xFF7D788E)
                            )
                        }
                    }

                    Switch(
                        checked = isPeriod,
                        onCheckedChange = { checked ->
                            viewModel.updateSelectedDateLog(
                                isPeriod = checked,
                                flow = flow,
                                symptoms = selectedSymptoms,
                                mood = selectedMood,
                                notes = notesText
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFF7096)
                        ),
                        modifier = Modifier.testTag("period_log_switch")
                    )
                }

                // If period is active, show the FLOW intensity capsules!
                AnimatedVisibility(
                    visible = isPeriod,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "Flow Intensity",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4458),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Light", "Medium", "Heavy").forEach { level ->
                                val flowsSelected = flow == level
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .shadow(if (flowsSelected) 4.dp else 1.dp, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (flowsSelected) Color(0xFFFFE4E6) else Color.White.copy(alpha = 0.4f)
                                        )
                                        .border(
                                            1.dp,
                                            if (flowsSelected) Color(0xFFFF7096) else Color.White.copy(alpha = 0.5f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            viewModel.updateSelectedDateLog(
                                                isPeriod = isPeriod,
                                                flow = level,
                                                symptoms = selectedSymptoms,
                                                mood = selectedMood,
                                                notes = notesText
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (flowsSelected) Color(0xFFFF4D6D) else Color(0xFF4A4458)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Symptoms Interactive Grid
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Track Symptoms",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D283E),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Grid of items wrapping in simple rows (efficient to prevent complex grid recomposition)
                val chunkedSymptoms = symptomsList.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    chunkedSymptoms.forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowItems.forEach { item ->
                                val isChecked = selectedSymptoms.contains(item.name)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .shadow(if (isChecked) 3.dp else 1.dp, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isChecked) Color(0xFFFFE4E6).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.3f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isChecked) Color(0xFFFF7096) else Color.White.copy(alpha = 0.5f),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            viewModel.toggleSymptomForSelectedDate(item.name)
                                        }
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = item.emoji, fontSize = 16.sp)
                                        Text(
                                            text = item.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isChecked) Color(0xFFFF5277) else Color(0xFF4A4458)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Mood row elements
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Log Mood State",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D283E),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(moodsList) { item ->
                        val isChecked = selectedMood == item.name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(70.dp)
                                .clickable { viewModel.updateMoodForSelectedDate(item.name) }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .shadow(if (isChecked) 6.dp else 1.dp, CircleShape)
                                    .background(
                                        if (isChecked) item.color else Color.White.copy(alpha = 0.4f),
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        if (isChecked) Color(0xFFFF5277) else Color.White.copy(alpha = 0.5f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.emoji, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.name,
                                fontSize = 10.sp,
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                                color = if (isChecked) Color(0xFFFF5277) else Color(0xFF7D788E),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 4. Notes input box
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Self-Care Notes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D283E),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextField(
                    value = notesText,
                    onValueChange = { viewModel.updateNotesForSelectedDate(it) },
                    placeholder = { Text("Write about your body, energy, meals, or reflections...", fontSize = 13.sp, color = Color(0xFF7D788E)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFF1F2).copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                        focusedIndicatorColor = Color(0xFFFF7096),
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("notes_textfield")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

data class SymptomItem(
    val name: String,
    val emoji: String
)

data class MoodItem(
    val name: String,
    val emoji: String,
    val color: Color
)
