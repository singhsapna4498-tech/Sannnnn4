package com.example.data.model

data class DeviceHardware(
    val brand: String = "Not available",
    val model: String = "Not available",
    val manufacturer: String = "Not available",
    val processor: String = "Not available",
    val cpuCores: Int = 0,
    val cpuArchitecture: String = "Not available",
    val gpuRenderer: String = "Not available",
    val totalRamGb: Float = 0f,
    val availableRamGb: Float = 0f,
    val totalStorageGb: Float = 0f,
    val availableStorageGb: Float = 0f,
    val androidVersion: String = "Not available",
    val screenResolution: String = "Not available",
    val screenDiagonalInches: String = "Not available",
    val screenDensityDpi: Int = 0,
    val currentRefreshRate: Float = 60f,
    val supportedRefreshRates: List<Float> = emptyList(),
    val batteryPct: Int = -1,
    val batteryTempC: Float = -1f,
    val thermalStatus: String = "Not available",
    val openGlEsVersion: String = "Not available",
    val touchSamplingRate: String = "Not available",
    val assignedTier: DeviceTier = DeviceTier.MID_RANGE
)
