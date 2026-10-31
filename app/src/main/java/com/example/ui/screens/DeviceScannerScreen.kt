package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceHardware
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DeviceScannerScreen(
    hardware: DeviceHardware,
    onRescan: () -> Unit,
    onProceedToGenerate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp)
            .testTag("device_scanner_screen")
    ) {
        item {
            TacticalHeader(
                title = "HARDWARE SCANNER",
                subtitle = "Hardware parameters detected via public Android System APIs"
            )

            // Tier Classification Banner
            GamingCard(
                borderColor = NeonRed,
                testTag = "scanner_tier_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DETECTED PERFORMANCE TIER",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = hardware.assignedTier.label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonRed
                        )
                        Text(
                            text = hardware.assignedTier.description,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Tier badge",
                        tint = NeonRed,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "DEVICE IDENTIFICATION",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            SpecItem("Brand", hardware.brand)
            SpecItem("Model", hardware.model)
            SpecItem("Manufacturer", hardware.manufacturer)
            SpecItem("Android Version", hardware.androidVersion)

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "PROCESSOR & GRAPHICS",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            SpecItem("Processor / Chipset", hardware.processor)
            SpecItem("CPU Architecture", hardware.cpuArchitecture)
            SpecItem("CPU Active Cores", "${hardware.cpuCores} Cores")
            SpecItem("GPU Renderer", hardware.gpuRenderer)
            SpecItem("OpenGL ES Version", hardware.openGlEsVersion)

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "MEMORY & STORAGE",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            SpecItem("Total RAM", "${hardware.totalRamGb} GB", highlight = true)
            SpecItem("Available RAM", "${hardware.availableRamGb} GB")
            SpecItem("Total Storage", "${hardware.totalStorageGb} GB")
            SpecItem("Available Storage", "${hardware.availableStorageGb} GB")

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "DISPLAY & REFRESH RATE",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            SpecItem("Screen Resolution", hardware.screenResolution)
            SpecItem("Calculated Screen Diagonal", hardware.screenDiagonalInches)
            SpecItem("Screen Density", "${hardware.screenDensityDpi} DPI")
            SpecItem("Current Refresh Rate", "${hardware.currentRefreshRate.toInt()} Hz", highlight = true)
            val supportedRatesString = if (hardware.supportedRefreshRates.isNotEmpty()) {
                hardware.supportedRefreshRates.map { "${it.toInt()}Hz" }.joinToString(", ")
            } else {
                "Not available"
            }
            SpecItem("Supported Refresh Rates", supportedRatesString)
            SpecItem("Touch Sampling Rate", hardware.touchSamplingRate) // Safely displays "Not available"

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "BATTERY & THERMAL STATUS",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            SpecItem("Battery Level", if (hardware.batteryPct >= 0) "${hardware.batteryPct}%" else "Not available")
            SpecItem("Battery Temperature", if (hardware.batteryTempC > 0) "${hardware.batteryTempC} °C" else "Not available")
            SpecItem("Thermal Status", hardware.thermalStatus)

            Spacer(modifier = Modifier.height(20.dp))
            TacticalButton(
                text = "CALCULATE SENSITIVITY WITH THIS SCAN",
                icon = Icons.Default.AutoFixHigh,
                color = NeonCyan,
                testTag = "btn_proceed_calc",
                onClick = onProceedToGenerate
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onRescan,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("btn_rescan"),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Rescan", tint = TextSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RE-SCAN HARDWARE", color = TextSecondary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
