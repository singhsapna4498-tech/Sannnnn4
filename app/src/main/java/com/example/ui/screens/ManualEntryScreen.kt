package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceHardware
import com.example.data.model.DeviceTier
import com.example.ui.components.TacticalButton
import com.example.ui.components.TacticalHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryScreen(
    initialHardware: DeviceHardware,
    onSaveManualSpecs: (DeviceHardware) -> Unit
) {
    val scroll = rememberScrollState()

    var brand by remember { mutableStateOf(if (initialHardware.brand != "Not available") initialHardware.brand else "") }
    var model by remember { mutableStateOf(if (initialHardware.model != "Not available") initialHardware.model else "") }
    var processor by remember { mutableStateOf(if (initialHardware.processor != "Not available") initialHardware.processor else "") }
    var gpu by remember { mutableStateOf(if (initialHardware.gpuRenderer != "Not available") initialHardware.gpuRenderer else "") }
    var ram by remember { mutableStateOf(if (initialHardware.totalRamGb > 0) initialHardware.totalRamGb.toInt().toString() else "") }
    var storage by remember { mutableStateOf(if (initialHardware.totalStorageGb > 0) initialHardware.totalStorageGb.toInt().toString() else "") }
    var resolution by remember { mutableStateOf(if (initialHardware.screenResolution != "Not available") initialHardware.screenResolution else "1080 x 2400 Px") }
    var refreshRate by remember { mutableStateOf(initialHardware.currentRefreshRate.toInt().toString()) }
    var androidVer by remember { mutableStateOf(if (initialHardware.androidVersion != "Not available") initialHardware.androidVersion else "Android 14") }
    var touchRate by remember { mutableStateOf(if (initialHardware.touchSamplingRate != "Not available") initialHardware.touchSamplingRate else "") }
    var selectedTier by remember { mutableStateOf(initialHardware.assignedTier) }

    var tierExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .verticalScroll(scroll)
            .padding(16.dp)
            .testTag("manual_entry_screen")
    ) {
        TacticalHeader(
            title = "MANUAL DEVICE ENTRY",
            subtitle = "Override or input custom phone hardware specifications"
        )

        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it; errorMessage = null },
            label = { Text("Brand / Manufacturer (e.g. Xiaomi, OnePlus, Samsung)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_brand"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = model,
            onValueChange = { model = it; errorMessage = null },
            label = { Text("Phone Model (e.g. Redmi Note 13, iQOO Neo 9)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_model"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = processor,
            onValueChange = { processor = it },
            label = { Text("Processor / Chipset (e.g. Snapdragon 8 Gen 2 / Dimensity 8300)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_processor"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = gpu,
            onValueChange = { gpu = it },
            label = { Text("GPU Renderer (e.g. Adreno 740 / Mali-G615)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_gpu"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = ram,
                onValueChange = { ram = it; errorMessage = null },
                label = { Text("Total RAM (GB)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_ram"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle
                )
            )
            OutlinedTextField(
                value = storage,
                onValueChange = { storage = it },
                label = { Text("Storage (GB)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_storage"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = refreshRate,
                onValueChange = { refreshRate = it; errorMessage = null },
                label = { Text("Refresh Rate (Hz)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_refresh_rate"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle
                )
            )
            OutlinedTextField(
                value = touchRate,
                onValueChange = { touchRate = it },
                label = { Text("Touch Rate (Hz, opt)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_touch_rate"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = resolution,
            onValueChange = { resolution = it },
            label = { Text("Screen Resolution (e.g. 1080 x 2400 Px)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_resolution"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = androidVer,
            onValueChange = { androidVer = it },
            label = { Text("Android Version (e.g. Android 14)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_android_version"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle
            )
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Device Performance Category Selector
        ExposedDropdownMenuBox(
            expanded = tierExpanded,
            onExpandedChange = { tierExpanded = !tierExpanded },
            modifier = Modifier.testTag("dropdown_tier")
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedTier.label,
                onValueChange = {},
                label = { Text("Device Performance Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tierExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle
                )
            )
            ExposedDropdownMenu(
                expanded = tierExpanded,
                onDismissRequest = { tierExpanded = false }
            ) {
                DeviceTier.values().forEach { tier ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(tier.label, fontWeight = FontWeight.Bold)
                                Text(tier.description, fontSize = 11.sp, color = TextSecondary)
                            }
                        },
                        onClick = {
                            selectedTier = tier
                            tierExpanded = false
                        }
                    )
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage!!,
                color = NeonRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("error_message")
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TacticalButton(
            text = "SAVE MANUAL SPECIFICATIONS",
            icon = Icons.Default.Save,
            color = NeonYellow,
            testTag = "btn_save_manual",
            onClick = {
                val parsedRam = ram.toFloatOrNull()
                val parsedHz = refreshRate.toFloatOrNull()

                if (brand.isBlank() || model.isBlank()) {
                    errorMessage = "Phone brand and model cannot be empty."
                } else if (parsedRam == null || parsedRam <= 0f) {
                    errorMessage = "Please enter a valid RAM number (e.g. 4, 6, 8, 12)."
                } else if (parsedHz == null || parsedHz <= 0f) {
                    errorMessage = "Please enter a valid display refresh rate (e.g. 60, 90, 120)."
                } else {
                    errorMessage = null
                    val custom = initialHardware.copy(
                        brand = brand.trim(),
                        model = model.trim(),
                        processor = if (processor.isBlank()) "Not available" else processor.trim(),
                        gpuRenderer = if (gpu.isBlank()) "Not available" else gpu.trim(),
                        totalRamGb = parsedRam,
                        availableRamGb = (parsedRam * 0.5f * 10).toInt() / 10f,
                        totalStorageGb = storage.toFloatOrNull() ?: 64f,
                        availableStorageGb = ((storage.toFloatOrNull() ?: 64f) * 0.4f * 10).toInt() / 10f,
                        screenResolution = resolution.trim(),
                        currentRefreshRate = parsedHz,
                        androidVersion = androidVer.trim(),
                        touchSamplingRate = if (touchRate.isBlank()) "Not available" else "${touchRate.trim()} Hz",
                        assignedTier = selectedTier
                    )
                    onSaveManualSpecs(custom)
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
