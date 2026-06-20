package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AuraCalendarLog
import com.example.ui.components.AuraDashboard
import com.example.ui.components.AuraJourneySettings
import com.example.ui.components.FloatingPastelOrbs
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AuraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainAppScreen(
    viewModel: AuraViewModel = viewModel()
) {
    var activeTab by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // GPU-accelerated soothing floating 60fps pastel aura background
        FloatingPastelOrbs()

        // Core visual scaffold with transparent sheets so aura drifts behind
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White.copy(alpha = 0.45f),
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { Icon(Icons.Default.Spa, contentDescription = "Bloom tab") },
                        label = { Text("Aura", fontSize = 11.sp, modifier = Modifier.testTag("tab_label_aura")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF5277),
                            unselectedIconColor = Color(0xFF7D788E),
                            selectedTextColor = Color(0xFFFF5277),
                            unselectedTextColor = Color(0xFF7D788E),
                            indicatorColor = Color(0xFFFFE4E6)
                        ),
                        modifier = Modifier.testTag("nav_btn_aura")
                    )

                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Journal tab") },
                        label = { Text("Journal", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF5277),
                            unselectedIconColor = Color(0xFF7D788E),
                            selectedTextColor = Color(0xFFFF5277),
                            unselectedTextColor = Color(0xFF7D788E),
                            indicatorColor = Color(0xFFFFE4E6)
                        ),
                        modifier = Modifier.testTag("nav_btn_journal")
                    )

                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Map and settings tab") },
                        label = { Text("Map", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF5277),
                            unselectedIconColor = Color(0xFF7D788E),
                            selectedTextColor = Color(0xFFFF5277),
                            unselectedTextColor = Color(0xFF7D788E),
                            indicatorColor = Color(0xFFFFE4E6)
                        ),
                        modifier = Modifier.testTag("nav_btn_map")
                    )
                }
            }
        ) { innerPadding ->
            // Simple fluid horizontal or crossfade screen loader
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                Crossfade(
                    targetState = activeTab, 
                    label = "Aura page transition",
                    modifier = Modifier.fillMaxSize()
                ) { tab ->
                    when (tab) {
                        0 -> AuraDashboard(viewModel = viewModel)
                        1 -> AuraCalendarLog(viewModel = viewModel)
                        2 -> AuraJourneySettings(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
