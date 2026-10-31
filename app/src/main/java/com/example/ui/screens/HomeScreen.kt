package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceHardware
import com.example.ui.components.GamingCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    hardware: DeviceHardware,
    onNavigate: (String) -> Unit
) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .verticalScroll(scroll)
            .padding(16.dp)
            .testTag("home_screen")
    ) {
        // App Header Banner
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MX SENSE AI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "SCAN • ANALYZE • GENERATE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonRed,
                        letterSpacing = 2.sp
                    )
                }
                Surface(
                    color = NeonCyanGlow,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, NeonCyan)
                ) {
                    Text(
                        text = "PRO V2.5",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Text(
                text = "Universal Gaming Sensitivity & Hardware Assistant for Free Fire India",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Active Hardware Summary Bar
        GamingCard(
            borderColor = NeonCyan,
            testTag = "device_summary_card"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(NeonCyanGlow, RoundedCornerShape(10.dp))
                        .border(BorderStroke(1.dp, NeonCyan), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Device icon",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val deviceDisplayName = if (hardware.brand != "Not available" && hardware.model != "Not available") {
                        "${hardware.brand} ${hardware.model}"
                    } else if (hardware.model != "Not available") {
                        hardware.model
                    } else {
                        "Hardware Not Scanned"
                    }
                    Text(
                        text = deviceDisplayName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${hardware.totalRamGb} GB RAM • ${hardware.currentRefreshRate.toInt()}Hz • ${hardware.assignedTier.label}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "TACTICAL OPERATIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NeonRed,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 6 Main Feature Buttons
        MenuActionCard(
            title = "1. SCAN MY DEVICE",
            subtitle = "Extract CPU, RAM, OpenGL & refresh rate via Android APIs",
            icon = Icons.Default.QrCodeScanner,
            accentColor = NeonCyan,
            testTag = "nav_scan_device",
            onClick = { onNavigate("scanner") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        MenuActionCard(
            title = "2. MANUAL DEVICE ENTRY",
            subtitle = "Custom entry for RAM, GPU, refresh rate & touch response",
            icon = Icons.Default.EditNote,
            accentColor = NeonYellow,
            testTag = "nav_manual_entry",
            onClick = { onNavigate("manual_entry") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        MenuActionCard(
            title = "3. GENERATE AI SENSITIVITY",
            subtitle = "Calculate Red Dot, General, Scope & Button Size values",
            icon = Icons.Default.AutoFixHigh,
            accentColor = NeonRed,
            testTag = "nav_generate_sensitivity",
            onClick = { onNavigate("sensitivity") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        MenuActionCard(
            title = "4. AI HEADSHOT CALIBRATION",
            subtitle = "Fix over-aiming, chest stuck crosshairs & inconsistent recoil",
            icon = Icons.Default.Adjust,
            accentColor = NeonGreen,
            testTag = "nav_calibration",
            onClick = { onNavigate("calibration") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        MenuActionCard(
            title = "5. GAMING OPTIMIZER",
            subtitle = "Recommended graphics, FPS, thermal & background tuning",
            icon = Icons.Default.Speed,
            accentColor = NeonCyan,
            testTag = "nav_gaming_optimizer",
            onClick = { onNavigate("optimizer") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        MenuActionCard(
            title = "6. SAVED PROFILES",
            subtitle = "Manage, compare side-by-side, rename, delete & share",
            icon = Icons.Default.Bookmarks,
            accentColor = PurpleAccent,
            testTag = "nav_saved_profiles",
            onClick = { onNavigate("saved_profiles") }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Compliance & Fair Play Disclaimer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = DarkSurface
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Shield Security",
                    tint = NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "MX SENSE AI operates 100% independently from Free Fire India. It does not inject code, manipulate memory, or access game files. All values are recommendations for manual in-game setup.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MenuActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() }
            .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .border(BorderStroke(1.dp, accentColor), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = TextMuted
            )
        }
    }
}
