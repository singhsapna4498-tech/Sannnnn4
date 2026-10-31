package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.MXSenseTheme
import com.example.ui.theme.NeonCyan

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MXSenseTheme {
                var currentScreen by remember { mutableStateOf("home") }
                val hardware by viewModel.hardwareSpecs.collectAsState()
                val profiles by viewModel.savedProfiles.collectAsState()
                val calibrationOffsets by viewModel.calibrationOffsets.collectAsState()

                BackHandler(enabled = currentScreen != "home") {
                    currentScreen = "home"
                }

                val screenTitle = when (currentScreen) {
                    "scanner" -> "DEVICE SCANNER"
                    "manual_entry" -> "MANUAL ENTRY"
                    "sensitivity" -> "AI SENSITIVITY"
                    "calibration" -> "HEADSHOT CALIBRATION"
                    "optimizer" -> "GAMING OPTIMIZER"
                    "saved_profiles" -> "SAVED PROFILES"
                    else -> "MX SENSE AI"
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    containerColor = CyberBlack,
                    topBar = {
                        if (currentScreen != "home") {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = screenTitle,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { currentScreen = "home" },
                                        modifier = Modifier.testTag("nav_back_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back to home",
                                            tint = NeonCyan
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBlack)
                            )
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = CyberBlack
                    ) {
                        when (currentScreen) {
                            "home" -> HomeScreen(
                                hardware = hardware,
                                onNavigate = { screen -> currentScreen = screen }
                            )
                            "scanner" -> DeviceScannerScreen(
                                hardware = hardware,
                                onRescan = { viewModel.scanHardware() },
                                onProceedToGenerate = { currentScreen = "sensitivity" }
                            )
                            "manual_entry" -> ManualEntryScreen(
                                initialHardware = hardware,
                                onSaveManualSpecs = { customSpecs ->
                                    viewModel.updateManualHardware(customSpecs)
                                    currentScreen = "sensitivity"
                                }
                            )
                            "sensitivity" -> SensitivityGeneratorScreen(
                                hardware = hardware,
                                calibrationOffsets = calibrationOffsets,
                                onSaveProfile = { profile -> viewModel.saveProfile(profile) }
                            )
                            "calibration" -> CalibrationScreen(
                                currentOffsets = calibrationOffsets,
                                onApplyOffsets = { offsets ->
                                    viewModel.applyCalibrationOffsets(offsets)
                                    currentScreen = "sensitivity"
                                }
                            )
                            "optimizer" -> GameOptimizerScreen(hardware = hardware)
                            "saved_profiles" -> SavedProfilesScreen(
                                profiles = profiles,
                                onDeleteProfile = { profile -> viewModel.deleteProfile(profile) },
                                onRenameProfile = { profile, name -> viewModel.renameProfile(profile, name) },
                                onRegenerateSensitivity = { currentScreen = "sensitivity" }
                            )
                        }
                    }
                }
            }
        }
    }
}
