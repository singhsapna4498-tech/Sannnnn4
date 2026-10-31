package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CalibrationOffsets
import com.example.ui.components.GamingCard
import com.example.ui.components.TacticalButton
import com.example.ui.components.TacticalHeader
import com.example.ui.theme.*

@Composable
fun CalibrationScreen(
    currentOffsets: CalibrationOffsets,
    onApplyOffsets: (CalibrationOffsets) -> Unit
) {
    val scroll = rememberScrollState()

    var aimAboveHead by remember { mutableStateOf(currentOffsets.generalOffset < 0) }
    var aimBelowHead by remember { mutableStateOf(currentOffsets.generalOffset > 0) }
    var aimTooSlow by remember { mutableStateOf(currentOffsets.redDotOffset > 2) }
    var aimTooQuick by remember { mutableStateOf(currentOffsets.redDotOffset < -2) }
    var inconsistentHeadshots by remember { mutableStateOf(currentOffsets.scope2xOffset != 0) }
    var calibrationMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .verticalScroll(scroll)
            .padding(16.dp)
            .testTag("calibration_screen")
    ) {
        TacticalHeader(
            title = "AI HEADSHOT CALIBRATION",
            subtitle = "Recoil & crosshair diagnostic to calculate sensitivity offsets"
        )

        GamingCard(
            borderColor = NeonYellow,
            testTag = "calibration_diagnostic_card"
        ) {
            Text(
                text = "CROSSHAIR DIAGNOSTIC QUESTIONS",
                color = NeonYellow,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "Select what happens during your drag headshot attempts in training ground or ranked matches:",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            DiagnosticToggle(
                label = "1. Is the aim flying ABOVE the enemy head?",
                checked = aimAboveHead,
                testTag = "toggle_aim_above",
                onCheckedChange = {
                    aimAboveHead = it
                    if (it) aimBelowHead = false
                    calibrationMessage = null
                }
            )

            DiagnosticToggle(
                label = "2. Is the aim staying STUCK ON CHEST / below head?",
                checked = aimBelowHead,
                testTag = "toggle_aim_below",
                onCheckedChange = {
                    aimBelowHead = it
                    if (it) aimAboveHead = false
                    calibrationMessage = null
                }
            )

            DiagnosticToggle(
                label = "3. Is the crosshair moving TOO SLOWLY during drag?",
                checked = aimTooSlow,
                testTag = "toggle_aim_slow",
                onCheckedChange = {
                    aimTooSlow = it
                    if (it) aimTooQuick = false
                    calibrationMessage = null
                }
            )

            DiagnosticToggle(
                label = "4. Is the crosshair moving TOO QUICKLY / unstable?",
                checked = aimTooQuick,
                testTag = "toggle_aim_quick",
                onCheckedChange = {
                    aimTooQuick = it
                    if (it) aimTooSlow = false
                    calibrationMessage = null
                }
            )

            DiagnosticToggle(
                label = "5. Are headshots INCONSISTENT across matches?",
                checked = inconsistentHeadshots,
                testTag = "toggle_inconsistent",
                onCheckedChange = {
                    inconsistentHeadshots = it
                    calibrationMessage = null
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostic Explanation Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "NO FALSE PROMISES GUARANTEE",
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "No tool can guarantee 100% headshots because in-game spread, enemy movement velocity, and your physical finger drag angle determine hit registration. This diagnostic systematically tunes physical pixel drag distance to match your muscle memory.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        TacticalButton(
            text = "CALCULATE & APPLY CALIBRATION",
            icon = Icons.Default.Check,
            color = NeonGreen,
            testTag = "btn_apply_calibration",
            onClick = {
                var genOffset = 0
                var redDotOffset = 0
                var buttonOffset = 0

                if (aimAboveHead) {
                    genOffset -= 4
                    redDotOffset -= 3
                    buttonOffset += 2 // Larger button reduces drag stroke speed
                }
                if (aimBelowHead) {
                    genOffset += 5
                    redDotOffset += 4
                    buttonOffset -= 2 // Smaller button allows sharper upward drag
                }
                if (aimTooSlow) {
                    genOffset += 3
                    redDotOffset += 3
                }
                if (aimTooQuick) {
                    genOffset -= 3
                    redDotOffset -= 2
                }
                if (inconsistentHeadshots) {
                    redDotOffset -= 2
                }

                val calculated = CalibrationOffsets(
                    generalOffset = genOffset,
                    redDotOffset = redDotOffset,
                    scope2xOffset = if (inconsistentHeadshots) -2 else 0,
                    buttonSizeOffset = buttonOffset
                )
                onApplyOffsets(calculated)
                calibrationMessage = "Diagnostic calibration offsets successfully applied to AI Sensitivity Engine!"
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                aimAboveHead = false
                aimBelowHead = false
                aimTooSlow = false
                aimTooQuick = false
                inconsistentHeadshots = false
                onApplyOffsets(CalibrationOffsets())
                calibrationMessage = "Calibration reset to factory defaults."
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .testTag("btn_reset_calibration"),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = TextSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("RESET TO DEFAULT OFFSETS", color = TextSecondary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }

        if (calibrationMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = calibrationMessage!!,
                color = NeonGreen,
                fontSize = 12.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.testTag("calibration_result_message")
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DiagnosticToggle(
    label: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = DarkSurface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonCyan,
                    checkedTrackColor = DarkSurfaceElevated,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = DarkSurface
                )
            )
        }
    }
}
