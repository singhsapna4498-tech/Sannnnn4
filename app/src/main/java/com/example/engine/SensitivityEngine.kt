package com.example.engine

import com.example.data.model.*

data class CalibrationOffsets(
    val generalOffset: Int = 0,
    val redDotOffset: Int = 0,
    val scope2xOffset: Int = 0,
    val scope4xOffset: Int = 0,
    val sniperOffset: Int = 0,
    val freeLookOffset: Int = 0,
    val buttonSizeOffset: Int = 0
)

data class OptimizationResult(
    val deviceTier: DeviceTier,
    val recommendedGraphics: String,
    val recommendedFps: String,
    val highResolution: String,
    val shadows: String,
    val autoScaleNotch: String,
    val batteryProfile: String,
    val performanceNotes: List<String>
)

object SensitivityEngine {

    fun calculateSensitivity(
        hardware: DeviceHardware,
        style: PlayStyle,
        weapon: WeaponPreference,
        grip: FingerControl,
        calibrationOffsets: CalibrationOffsets = CalibrationOffsets(),
        userFeedbackRating: Int = 0
    ): SensitivityProfile {
        // Base General Sensitivity (0 - 100 in-game scale)
        // Devices with slower processors/refresh rate need maximum drag response (98-100).
        // High-end & Flagship with high touch response (120Hz/240Hz polling) benefit from slightly reduced
        // sensitivity to avoid overshooting past the head.
        var general = when (hardware.assignedTier) {
            DeviceTier.LOW_END -> 100
            DeviceTier.BUDGET -> 98
            DeviceTier.MID_RANGE -> 95
            DeviceTier.HIGH_END -> 91
            DeviceTier.FLAGSHIP -> 86
        }

        // Adjust for Display Refresh Rate
        if (hardware.currentRefreshRate >= 120f) {
            general -= 3
        } else if (hardware.currentRefreshRate <= 60f) {
            general += 2
        }

        // Base values for scopes
        var redDot = general - 4
        var scope2x = general - 12
        var scope4x = general - 20
        var sniperScope = 52
        var freeLook = 75

        // Adjust for PlayStyle
        when (style) {
            PlayStyle.RUSH -> {
                general = (general + 4).coerceAtMost(100)
                redDot = (redDot + 3).coerceAtMost(100)
                freeLook = 86
            }
            PlayStyle.LONG_RANGE -> {
                general -= 2
                scope2x += 2
                scope4x += 3
            }
            PlayStyle.SNIPER -> {
                general -= 4
                sniperScope = 42
                scope4x -= 4
            }
            PlayStyle.BALANCED -> {
                // Neutral baseline
            }
        }

        // Adjust for Weapon Preference
        when (weapon) {
            WeaponPreference.M1887 -> {
                // Shotgun flick headshots need high drag velocity
                general = (general + 3).coerceAtMost(100)
                redDot = (redDot + 4).coerceAtMost(100)
            }
            WeaponPreference.MP40 -> {
                // High rate-of-fire spray tracking requires red dot and 2x stability
                redDot = (redDot + 2).coerceAtMost(100)
                scope2x = (scope2x + 3).coerceAtMost(100)
            }
            WeaponPreference.DESERT_EAGLE -> {
                // Single drag flick one-tap
                general = (general + 4).coerceAtMost(100)
                redDot = (redDot + 2).coerceAtMost(100)
            }
            WeaponPreference.SNIPER -> {
                sniperScope = 38
            }
            WeaponPreference.ALL_WEAPONS -> {}
        }

        // Adjust for Finger Control ergonomics
        when (grip) {
            FingerControl.TWO_FINGER -> {
                // 2 Thumbs have limited drag distance on mobile screens
                general = (general + 2).coerceAtMost(100)
            }
            FingerControl.THREE_FINGER -> {
                // Neutral
            }
            FingerControl.FOUR_FINGER -> {
                // Claw players have superior micro-aiming stability
                general = (general - 2).coerceAtLeast(60)
            }
            FingerControl.FIVE_FINGER -> {
                general = (general - 4).coerceAtLeast(60)
            }
        }

        // Rating-based self improvement (if user rated 1-2 stars, calibrate towards balance; if 4-5 stars, preserve)
        var ratingModifier = 0
        if (userFeedbackRating in 1..2) {
            ratingModifier = -2
        }

        // Apply interactive Calibration Offsets
        general = (general + calibrationOffsets.generalOffset + ratingModifier).coerceIn(40, 100)
        redDot = (redDot + calibrationOffsets.redDotOffset + ratingModifier).coerceIn(35, 100)
        scope2x = (scope2x + calibrationOffsets.scope2xOffset).coerceIn(30, 100)
        scope4x = (scope4x + calibrationOffsets.scope4xOffset).coerceIn(25, 100)
        sniperScope = (sniperScope + calibrationOffsets.sniperOffset).coerceIn(15, 80)
        freeLook = (freeLook + calibrationOffsets.freeLookOffset).coerceIn(30, 100)

        // Recommended Fire Button Size (% HUD scale)
        val buttonSize = when {
            hardware.assignedTier == DeviceTier.LOW_END -> 55
            grip == FingerControl.FOUR_FINGER || grip == FingerControl.FIVE_FINGER -> 42
            weapon == WeaponPreference.M1887 -> 44
            else -> 48
        } + calibrationOffsets.buttonSizeOffset

        // Recommended System Smallest Width (DPI in Developer Options)
        val recDpi = when (hardware.assignedTier) {
            DeviceTier.LOW_END -> 392
            DeviceTier.BUDGET -> 411
            DeviceTier.MID_RANGE -> 440
            DeviceTier.HIGH_END -> 480
            DeviceTier.FLAGSHIP -> 520
        }

        val deviceName = if (hardware.brand != "Not available" && hardware.model != "Not available") {
            "${hardware.brand} ${hardware.model}"
        } else if (hardware.model != "Not available") {
            hardware.model
        } else {
            "Custom Device"
        }

        return SensitivityProfile(
            name = "$deviceName [${weapon.weaponName}]",
            general = general,
            redDot = redDot,
            scope2x = scope2x,
            scope4x = scope4x,
            sniperScope = sniperScope,
            freeLook = freeLook,
            fireButtonSize = buttonSize.coerceIn(35, 65),
            recommendedDpi = recDpi,
            deviceModel = deviceName,
            playStyle = style.label,
            weaponPreference = weapon.label,
            fingerControl = grip.label,
            ratingScore = userFeedbackRating
        )
    }

    fun optimizeSettings(hardware: DeviceHardware): OptimizationResult {
        val tier = hardware.assignedTier
        val isWarm = hardware.batteryTempC >= 39f || hardware.thermalStatus.contains("Throttling", true)

        val graphics = when (tier) {
            DeviceTier.FLAGSHIP -> if (isWarm) "Standard" else "Ultra"
            DeviceTier.HIGH_END -> "Ultra"
            DeviceTier.MID_RANGE -> "Standard"
            DeviceTier.BUDGET -> "Smooth"
            DeviceTier.LOW_END -> "Smooth"
        }

        val fps = when (tier) {
            DeviceTier.LOW_END -> "Normal / High"
            else -> "High (60 / 90 / 120 FPS)"
        }

        val highRes = when (tier) {
            DeviceTier.FLAGSHIP, DeviceTier.HIGH_END -> if (isWarm) "Normal" else "High"
            else -> "Normal"
        }

        val shadows = if (tier == DeviceTier.FLAGSHIP && !isWarm) "On" else "Off"
        val autoScale = if (tier == DeviceTier.LOW_END || tier == DeviceTier.BUDGET) "Off" else "On"
        val batteryProf = "Unrestricted (In Android App Settings for Free Fire)"

        val notes = mutableListOf<String>()

        if (isWarm) {
            notes.add("Thermal Alert: Battery temperature is ${hardware.batteryTempC}°C. Keep Shadows OFF to prevent CPU thermal throttling and sudden frame drops.")
        } else {
            notes.add("Thermal Status: Device temperature is optimal. No active throttling risk detected.")
        }

        when (tier) {
            DeviceTier.FLAGSHIP -> {
                notes.add("Hardware Headroom: High processing capacity (${hardware.totalRamGb} GB RAM, ${hardware.currentRefreshRate.toInt()}Hz). Run High FPS mode for fluid swipe registration.")
            }
            DeviceTier.HIGH_END -> {
                notes.add("Graphics Stability: Ultra graphics with Shadows OFF guarantees zero frame drops during intense 4v4 squad rushes.")
            }
            DeviceTier.MID_RANGE -> {
                notes.add("Smooth Framerates: 'Standard' graphics is ideal for prolonged ranked grinding without phone overheating.")
            }
            DeviceTier.BUDGET -> {
                notes.add("Touch Optimization: 'Smooth' graphics minimizes input latency and maximizes drag headshot consistency on ${hardware.totalRamGb} GB RAM.")
                notes.add("RAM Hygiene: Close social media and background browsers before starting ranked matches to maximize the ${hardware.availableRamGb} GB available RAM.")
            }
            DeviceTier.LOW_END -> {
                notes.add("Latency Reduction: Set graphics to 'Smooth'. Restart phone before tournaments to clear memory buffers.")
                notes.add("Storage Hygiene: Ensure at least 15% free internal storage (${hardware.availableStorageGb} GB free now) to keep device swap space responsive.")
            }
        }

        notes.add("Physical Touch Friction: Clean your screen regularly; finger moisture or oil causes drag stutter. Powder or gaming thumb sleeves ensure uniform flick resistance.")
        notes.add("Fair Play Safety: All optimizations are recommendations to be set manually inside Free Fire settings. MX SENSE AI never modifies system kernels or game files.")

        return OptimizationResult(
            deviceTier = tier,
            recommendedGraphics = graphics,
            recommendedFps = fps,
            highResolution = highRes,
            shadows = shadows,
            autoScaleNotch = autoScale,
            batteryProfile = batteryProf,
            performanceNotes = notes
        )
    }
}
