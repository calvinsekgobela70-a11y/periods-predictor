package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AuraViewModel
import kotlin.math.roundToInt

@Composable
fun AuraJourneySettings(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val calculatedCycleLength by viewModel.calculatedCycleLength.collectAsState()
    val periodBlocks by viewModel.cyclePeriodBlocks.collectAsState()

    var editingName by remember { mutableStateOf(settings.userName) }
    var editingCycleLength by remember { mutableStateOf(settings.typicalCycleLength.toFloat()) }
    var editingPeriodLength by remember { mutableStateOf(settings.typicalPeriodLength.toFloat()) }

    // Sync editing states with loaded DB values in real-time
    LaunchedEffect(settings) {
        editingName = settings.userName
        editingCycleLength = settings.typicalCycleLength.toFloat()
        editingPeriodLength = settings.typicalPeriodLength.toFloat()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Topic Header
        Column {
            Text(
                text = "MY JOURNEY & CYCLE MAP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF472B6),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Cycle Parameters Calibration",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D283E)
            )
        }

        // 1. Core Cycle Stats Analytics Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Personal Biological Map",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D283E)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Estimated average Cycle Length (computed from logs)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFFFF1F2).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Calculated Cycle", fontSize = 11.sp, color = Color(0xFF7D788E), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$calculatedCycleLength Days",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF5277)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (periodBlocks.size >= 2) "Averaged from logs" else "Settings baseline",
                                fontSize = 9.sp,
                                color = Color(0xFF7D788E)
                            )
                        }
                    }

                    // Total Logs recorded
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE0E7FF).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Period Logs", fontSize = 11.sp, color = Color(0xFF7D788E), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${periodBlocks.size} Cycles",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4F46E5)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Stored in database", fontSize = 9.sp, color = Color(0xFF7D788E))
                        }
                    }
                }
            }
        }

        // 2. Interactive Settings Panel Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF1F2), CircleShape)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                            tint = Color(0xFFFF5277),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Customize Baseline",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D283E)
                    )
                }

                Divider(color = Color(0xFFFF7096).copy(alpha = 0.15f))

                // Name field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Your Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A4458))
                    TextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                            focusedIndicatorColor = Color(0xFFFF7096)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_settings_input")
                    )
                }

                // Typical cycle slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Typical Cycle Length",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4458)
                        )
                        Text(
                            text = "${editingCycleLength.roundToInt()} Days",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5277)
                        )
                    }

                    Slider(
                        value = editingCycleLength,
                        onValueChange = { editingCycleLength = it },
                        valueRange = 21f..40f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF5277),
                            activeTrackColor = Color(0xFFFF5277),
                            inactiveTrackColor = Color(0xFFFFD5C6).copy(alpha = 0.4f)
                        )
                    )
                }

                // Typical period length slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Typical Period Duration",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4458)
                        )
                        Text(
                            text = "${editingPeriodLength.roundToInt()} Days",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF7096)
                        )
                    }

                    Slider(
                        value = editingPeriodLength,
                        onValueChange = { editingPeriodLength = it },
                        valueRange = 3f..10f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF7096),
                            activeTrackColor = Color(0xFFFF7096),
                            inactiveTrackColor = Color(0xFFFFCAD4).copy(alpha = 0.4f)
                        )
                    )
                }

                // Save button
                Button(
                    onClick = {
                        viewModel.updateCycleSettings(
                            typicalCycle = editingCycleLength.roundToInt(),
                            typicalPeriod = editingPeriodLength.roundToInt(),
                            name = editingName
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7096)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_settings_button")
                ) {
                    Text("Apply Calibration", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 3. Informational Biological Guide Cards
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.HelpOutline,
                    contentDescription = null,
                    tint = Color(0xFF7D788E),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Understanding Your Phases",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D283E)
                )
            }

            PhaseInfoCard(
                phase = "1. Menstrual Phase (Winter)",
                description = "Estrogen and progesterone hit baseline lows. The body sheds its endometrial lining. Slow down, honor your need for sleep, and drink lots of warm liquids.",
                color = Color(0xFFFFB3C1).copy(alpha = 0.2f),
                indicatorColor = Color(0xFFFF5277)
            )

            PhaseInfoCard(
                phase = "2. Follicular Phase (Spring)",
                description = "The pituitary gland releases FSH to trigger follicle maturation. Estrogen begins climbing, replenishing cellular strength, energy levels, and bright cognitive focus.",
                color = Color(0xFFD8F3DC).copy(alpha = 0.2f),
                indicatorColor = Color(0xFF40916C)
            )

            PhaseInfoCard(
                phase = "3. Fertile Window & Ovulation (Summer)",
                description = "Luteinizing Hormone (LH) surges, causing the mature follicle to release an egg. Estrogen peaks, and you'll feel socially magnetic and fully vibrant.",
                color = Color(0xFFFFCAD4).copy(alpha = 0.2f),
                indicatorColor = Color(0xFFFF7096)
            )

            PhaseInfoCard(
                phase = "4. Luteal Phase (Autumn)",
                description = "Progesterone climbs rapidly to prepare the womb. If fertilisation doesn't occur, hormones dip, sometimes triggering pre-menstrual physical cramps or sensitive emotions.",
                color = Color(0xFFE8E8E4).copy(alpha = 0.2f),
                indicatorColor = Color(0xFF9B5DE5)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PhaseInfoCard(
    phase: String,
    description: String,
    color: Color,
    indicatorColor: Color
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(indicatorColor, RoundedCornerShape(2.dp))
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = phase, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = indicatorColor)
                Text(text = description, fontSize = 11.sp, color = Color(0xFF4A4458), lineHeight = 16.sp)
            }
        }
    }
}
