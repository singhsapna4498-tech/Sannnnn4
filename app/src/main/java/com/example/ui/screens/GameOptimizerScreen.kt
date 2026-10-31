package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceHardware
import com.example.engine.SensitivityEngine
import com.example.ui.components.GamingCard
import com.example.ui.components.SpecItem
import com.example.ui.components.TacticalHeader
import com.example.ui.theme.*

@Composable
fun GameOptimizerScreen(hardware: DeviceHardware) {
    val scroll = rememberScrollState()
    val optimization = SensitivityEngine.optimizeSettings(hardware)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .verticalScroll(scroll)
            .padding(16.dp)
            .testTag("game_optimizer_screen")
    ) {
        TacticalHeader(
            title = "GAMING OPTIMIZER",
            subtitle = "Hardware-aware settings recommendations for smooth frame delivery"
        )

        GamingCard(
            borderColor = NeonCyan,
            testTag = "optimizer_tier_card"
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Speedometer",
                    tint = NeonCyan,
                    modifier = Modifier.size(34.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "DEVICE PERFORMANCE TIER",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = optimization.deviceTier.label,
                        color = NeonCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "RECOMMENDED IN-GAME DISPLAY SETTINGS",
            color = NeonRed,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        SpecItem("Graphics Preset", optimization.recommendedGraphics, highlight = true)
        SpecItem("High FPS Setting", optimization.recommendedFps, highlight = true)
        SpecItem("High Resolution Buffer", optimization.highResolution)
        SpecItem("Shadows", optimization.shadows)
        SpecItem("Auto Scale Notch", optimization.autoScaleNotch)

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "HARDWARE STABILITY & THERMAL MANAGEMENT",
            color = NeonYellow,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        optimization.performanceNotes.forEachIndexed { index, note ->
            OptimizationAdviceItem(text = note, index = index)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hardware Reality Notice
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "TRANSPARENT SYSTEM DISCLOSURE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "MX SENSE AI operates strictly within Android user-space sandboxes. It does not manipulate kernel clocks, overclock hardware, or touch protected game memory. These recommendations optimize Android OS resource allocation and game renderer overhead.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OptimizationAdviceItem(text: String, index: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("advice_item_$index"),
        shape = RoundedCornerShape(8.dp),
        color = DarkSurfaceElevated
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Check",
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
