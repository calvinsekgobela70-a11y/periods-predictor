package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.CyclePhase
import com.example.viewmodel.CycleState
import com.example.data.api.GeminiClient
import com.example.viewmodel.AiInsightState
import com.example.viewmodel.AuraViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AuraDashboard(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val daysToNextPeriod by viewModel.daysToNextPeriod.collectAsState()
    val lastPeriodStart by viewModel.lastPeriodStartDate.collectAsState()
    val aiState by viewModel.aiInsightState.collectAsState()
    
    val selectedDateState = viewModel.getCycleStateForDate(LocalDate.now())
    val breathingValue by rememberBreathingScale(durationMillis = 3500)

    // Dynamic pastel gradient colors matching active menstrual phase
    val phaseGradients = when (selectedDateState.phase) {
        CyclePhase.MENSTRUAL -> listOf(Color(0xFFF472B6), Color(0xFFEC4899), Color(0xFFDB2777))
        CyclePhase.FOLLICULAR -> listOf(Color(0xFF34D399), Color(0xFF10B981), Color(0xFF059669))
        CyclePhase.FERTILE -> listOf(Color(0xFFFBCFE8), Color(0xFFF472B6), Color(0xFFBE185D))
        CyclePhase.OVULATION -> listOf(Color(0xFFFCE7F3), Color(0xFFF472B6), Color(0xFFE11D48))
        CyclePhase.LUTEAL -> listOf(Color(0xFFE9D5FF), Color(0xFFC084FC), Color(0xFF8B5CF6))
    }

    val phaseDisplayName = when (selectedDateState.phase) {
        CyclePhase.MENSTRUAL -> "Menstrual Phase"
        CyclePhase.FOLLICULAR -> "Follicular Phase"
        CyclePhase.FERTILE -> "Fertile Window"
        CyclePhase.OVULATION -> "Ovulation Day"
        CyclePhase.LUTEAL -> "Luteal Phase"
    }

    val phaseNickName = when (selectedDateState.phase) {
        CyclePhase.MENSTRUAL -> "Cozy Autumn Sanctuary"
        CyclePhase.FOLLICULAR -> "New Spring Awakenings"
        CyclePhase.FERTILE -> "High Blooming Summer"
        CyclePhase.OVULATION -> "Radiant Solstice Glow"
        CyclePhase.LUTEAL -> "Warm Hearth Winter"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Sleek Immersive Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GOOD MORNING, ${settings.userName.uppercase()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF472B6),
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D283E)
                )
            }

            // Glass small circular calendar button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White.copy(alpha = 0.4f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Cosmic calendar emblem",
                    tint = Color(0xFFF472B6),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. The Core Interactive Breathing Bloom Dial (Immersive Nested Dial)
        Box(
            modifier = Modifier
                .size(260.dp)
                .scale(breathingValue),
            contentAlignment = Alignment.Center
        ) {
            // Outer circular support ring
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp, Color(0xFFFFF1F2).copy(alpha = 0.5f), CircleShape)
            )
            // Accent sweep stroke border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp, Brush.sweepGradient(phaseGradients), CircleShape)
            )

            // Central glass-card bubble with localized pink drop glow matching styling specs
            Box(
                modifier = Modifier
                    .size(208.dp)
                    .shadow(16.dp, CircleShape, spotColor = Color(0xFFF472B6).copy(alpha = 0.25f))
                    .background(Color.White.copy(alpha = 0.45f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.65f), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (lastPeriodStart == null) "TRACK STATUS" else "NEXT PERIOD IN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEC4899),
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (lastPeriodStart == null) "READY" else "$daysToNextPeriod",
                        fontSize = 62.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2D283E),
                        textAlign = TextAlign.Center,
                        lineHeight = 62.sp
                    )

                    Text(
                        text = if (lastPeriodStart == null) "Log now" else "Days",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4A4458)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Minimal aesthetic badge
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF1F2), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = phaseDisplayName.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDB2777),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Small supportive subtitle
        Text(
            text = selectedDateState.phaseDescription,
            fontSize = 13.sp,
            color = Color(0xFF7D788E),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // 3. Current Phase Wisdom Overview (Premium Glass Card)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(phaseGradients[2], CircleShape)
                    )
                    Text(
                        text = phaseNickName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D283E)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Aura flow aligns beautifully to your calendar. Honor your internal cycles and pamper yourself today with customized healing actions.",
                    fontSize = 12.sp,
                    color = Color(0xFF4A4458),
                    lineHeight = 17.sp
                )
            }
        }

        // 4. Custom AI Insights Drawer Card styled with pink-purple linear gradient!
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFF472B6), Color(0xFFA78BFA))
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "Intelligence",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "DAILY INSIGHT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Aura's Wisdom Generator",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.25f))

                // Render dynamic states for AI call
                Crossfade(targetState = aiState, label = "AI Insight animation") { state ->
                    when (state) {
                        is AiInsightState.Idle -> {
                            val logs by viewModel.allLogs.collectAsState()
                            val latestLogWithSymptoms = logs.filter { it.symptoms.isNotEmpty() }.lastOrNull()
                            
                            val textToShow = if (latestLogWithSymptoms != null) {
                                "I see you logged symptoms recently. Tap below to let me compile custom soothing insights and personal wellness tips for you!"
                            } else {
                                "Hello, lovely! Currently, there aren't enough logs today. Keep tracking your flow and symptoms, then tap to run the AI engine!"
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = textToShow,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Button(
                                    onClick = { viewModel.fetchAiInsight() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFFDB2777)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ai_insight_button")
                                ) {
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Unveil AI Insight", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        is AiInsightState.Loading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(color = Color.White)
                                Text(
                                    text = "Consulting Aura's garden...",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        is AiInsightState.Success -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = state.insight,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                if (!GeminiClient.isKeyConfigured()) {
                                    // Gentle configuration warning badge
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "✨ Local Wisdom System is running offline as fallback. Set your GEMINI_API_KEY in the AI Studio secrets panel to unlock personalized LLM summaries!",
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.fetchAiInsight() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.2f),
                                        contentColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Refresh Insights", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        is AiInsightState.Error -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Error: ${state.message}",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )
                                Button(
                                    onClick = { viewModel.fetchAiInsight() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Try Again", color = Color(0xFFDB2777), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Soothing Self-Care Quick Routine panel
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's Soothing Rituals",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D283E),
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RitualCard("Warm Herbal Tea", "Rosehip & Ginger", Icons.Rounded.Spa, Color(0xFFFFD5C6))
                RitualCard("Stretching", "5M Soft Pelvic Openers", Icons.Default.Favorite, Color(0xFFD6E2E9))
                RitualCard("Cozy Healing Warmth", "Lower belly compress", Icons.Rounded.WaterDrop, Color(0xFFFDE2E4))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun RitualCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.4f)),
        modifier = Modifier
            .width(180.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .padding(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = subtitle, fontSize = 10.sp, color = Color.DarkGray)
            }
        }
    }
}
