package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.engine.CalibrationOffsets
import com.example.engine.SensitivityEngine
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensitivityGeneratorScreen(
    hardware: DeviceHardware,
    calibrationOffsets: CalibrationOffsets,
    onSaveProfile: (SensitivityProfile) -> Unit
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()

    var playStyle by remember { mutableStateOf(PlayStyle.RUSH) }
    var weapon by remember { mutableStateOf(WeaponPreference.M1887) }
    var grip by remember { mutableStateOf(FingerControl.TWO_FINGER) }
    var feedbackRating by remember { mutableIntStateOf(0) }
    var fineTuneGeneral by remember { mutableIntStateOf(0) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    val combinedOffsets = remember(calibrationOffsets, fineTuneGeneral) {
        calibrationOffsets.copy(
            generalOffset = calibrationOffsets.generalOffset + fineTuneGeneral,
            redDotOffset = calibrationOffsets.redDotOffset + fineTuneGeneral
        )
    }

    val profile = remember(hardware, playStyle, weapon, grip, combinedOffsets, feedbackRating) {
        SensitivityEngine.calculateSensitivity(
            hardware = hardware,
            style = playStyle,
            weapon = weapon,
            grip = grip,
            calibrationOffsets = combinedOffsets,
            userFeedbackRating = feedbackRating
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .verticalScroll(scroll)
            .padding(16.dp)
            .testTag("sensitivity_generator_screen")
    ) {
        TacticalHeader(
            title = "AI SENSITIVITY ENGINE",
            subtitle = "Custom mathematical calibration for Free Fire India"
        )

        // Dropdown Pickers for Player Personalization
        PickerDropdown(
            label = "Gameplay Style",
            options = PlayStyle.values().map { it.label },
            selectedIndex = PlayStyle.values().indexOf(playStyle),
            testTag = "dropdown_play_style",
            onSelect = { playStyle = PlayStyle.values()[it]; saveMessage = null }
        )
        Spacer(modifier = Modifier.height(8.dp))

        PickerDropdown(
            label = "Weapon Focus",
            options = WeaponPreference.values().map { it.label },
            selectedIndex = WeaponPreference.values().indexOf(weapon),
            testTag = "dropdown_weapon",
            onSelect = { weapon = WeaponPreference.values()[it]; saveMessage = null }
        )
        Spacer(modifier = Modifier.height(8.dp))

        PickerDropdown(
            label = "Finger Control Layout",
            options = FingerControl.values().map { it.label },
            selectedIndex = FingerControl.values().indexOf(grip),
            testTag = "dropdown_grip",
            onSelect = { grip = FingerControl.values()[it]; saveMessage = null }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sensitivity Outputs Card
        GamingCard(
            borderColor = NeonRed,
            testTag = "sensitivity_outputs_card"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPTIMAL IN-GAME SENSITIVITY",
                    color = NeonRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = hardware.assignedTier.label.take(15),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            SensitivityGauge("General (Drag Headshot)", profile.general, NeonRed)
            SensitivityGauge("Red Dot Sight", profile.redDot, NeonCyan)
            SensitivityGauge("2X Scope", profile.scope2x, NeonYellow)
            SensitivityGauge("4X Scope", profile.scope4x, NeonGreen)
            SensitivityGauge("Sniper Scope", profile.sniperScope, PurpleAccent)
            SensitivityGauge("Free Look 360", profile.freeLook, TextPrimary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // HUD / Phone DPI Tuning
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SpecDisplayCard(
                title = "FIRE BUTTON",
                value = "${profile.fireButtonSize}%",
                subtitle = "Optimal drag surface",
                modifier = Modifier.weight(1f)
            )
            SpecDisplayCard(
                title = "SYSTEM DPI",
                value = "${profile.recommendedDpi}",
                subtitle = "Developer Smallest Width",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Test & Improve: User Rating & Micro-Tuning
        GamingCard(
            borderColor = NeonYellow,
            testTag = "feedback_improvement_card"
        ) {
            Text(
                text = "TEST & IMPROVE RECOIL CALIBRATION",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeonYellow
            )
            Text(
                text = "Rate this configuration after trying it in training mode. AI self-tunes values based on your feedback.",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InteractiveStarRating(
                    rating = feedbackRating,
                    onRatingChanged = { newRating ->
                        feedbackRating = newRating
                        saveMessage = if (newRating >= 4) {
                            "Excellent! AI tuned profile for peak precision."
                        } else {
                            "AI recalibrated values to soften drag sensitivity."
                        }
                    }
                )

                Text(
                    text = if (feedbackRating > 0) "$feedbackRating / 5 Stars" else "Rate Accuracy",
                    fontSize = 12.sp,
                    color = NeonYellow,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Micro-Adjustment buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { fineTuneGeneral = (fineTuneGeneral - 2).coerceAtLeast(-8) },
                    border = BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_micro_decrease")
                ) {
                    Text("- Flying Over Head", color = NeonCyan, fontSize = 11.sp)
                }

                Text(
                    text = if (fineTuneGeneral > 0) "+$fineTuneGeneral" else "$fineTuneGeneral",
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontSize = 15.sp
                )

                OutlinedButton(
                    onClick = { fineTuneGeneral = (fineTuneGeneral + 2).coerceAtMost(8) },
                    border = BorderStroke(1.dp, NeonRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_micro_increase")
                ) {
                    Text("+ Stuck on Chest", color = NeonRed, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions: Save & Share
        TacticalButton(
            text = "SAVE SENSITIVITY PROFILE",
            icon = Icons.Default.Save,
            color = NeonCyan,
            testTag = "btn_save_profile",
            onClick = {
                onSaveProfile(profile)
                saveMessage = "Profile saved locally to Room Database!"
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { shareProfile(context, profile) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .testTag("btn_share_profile"),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, NeonRed)
        ) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = NeonRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SHARE SETTINGS WITH SQUAD",
                color = NeonRed,
                fontWeight = FontWeight.Bold
            )
        }

        if (saveMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = saveMessage!!,
                color = NeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("save_success_text")
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SpecDisplayCard(title: String, value: String, subtitle: String, modifier: Modifier) {
    Card(
        modifier = modifier.border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = TextSecondary)
            Text(
                value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan
            )
            Text(subtitle, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerDropdown(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    testTag: String,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.testTag(testTag)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = options.getOrElse(selectedIndex) { "" },
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun shareProfile(context: Context, profile: SensitivityProfile) {
    val message = """
        🎮 MX SENSE AI • SENSITIVITY CONFIG 🎮
        Device: ${profile.deviceModel}
        Combat Style: ${profile.playStyle}
        Weapon Focus: ${profile.weaponPreference}
        Control: ${profile.fingerControl}
        ------------------------------------
        • General: ${profile.general}
        • Red Dot: ${profile.redDot}
        • 2X Scope: ${profile.scope2x}
        • 4X Scope: ${profile.scope4x}
        • Sniper Scope: ${profile.sniperScope}
        • Free Look: ${profile.freeLook}
        ------------------------------------
        🎯 Fire Button: ${profile.fireButtonSize}%
        📱 Recommended DPI: ${profile.recommendedDpi}
        Generated by MX SENSE AI (Fair-Play Gaming Assistant)
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Free Fire Sensitivity Settings")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, "Share Sensitivity Settings"))
}
