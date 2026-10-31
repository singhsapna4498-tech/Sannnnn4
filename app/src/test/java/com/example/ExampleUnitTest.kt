package com.example

import com.example.data.model.*
import com.example.engine.CalibrationOffsets
import com.example.engine.SensitivityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testSensitivityBounds_AllTiersAndStyles() {
        val lowEndDevice = DeviceHardware(
            totalRamGb = 2f,
            currentRefreshRate = 60f,
            assignedTier = DeviceTier.LOW_END
        )
        val profile = SensitivityEngine.calculateSensitivity(
            hardware = lowEndDevice,
            style = PlayStyle.RUSH,
            weapon = WeaponPreference.M1887,
            grip = FingerControl.TWO_FINGER
        )

        assertTrue("General sensitivity must be within 0..100", profile.general in 0..100)
        assertTrue("Red Dot sensitivity must be within 0..100", profile.redDot in 0..100)
        assertTrue("2X Scope must be within 0..100", profile.scope2x in 0..100)
        assertTrue("4X Scope must be within 0..100", profile.scope4x in 0..100)
        assertTrue("Sniper Scope must be within 0..100", profile.sniperScope in 0..100)
        assertTrue("Free Look must be within 0..100", profile.freeLook in 0..100)
        assertTrue("Fire Button size must be between 35 and 65", profile.fireButtonSize in 35..65)
    }

    @Test
    fun testCalibrationOffsets_AlterOutput() {
        val midDevice = DeviceHardware(totalRamGb = 6f, assignedTier = DeviceTier.MID_RANGE)
        val normalProfile = SensitivityEngine.calculateSensitivity(
            hardware = midDevice,
            style = PlayStyle.BALANCED,
            weapon = WeaponPreference.ALL_WEAPONS,
            grip = FingerControl.TWO_FINGER,
            calibrationOffsets = CalibrationOffsets()
        )
        val calibratedProfile = SensitivityEngine.calculateSensitivity(
            hardware = midDevice,
            style = PlayStyle.BALANCED,
            weapon = WeaponPreference.ALL_WEAPONS,
            grip = FingerControl.TWO_FINGER,
            calibrationOffsets = CalibrationOffsets(generalOffset = -4, redDotOffset = -3)
        )

        assertEquals(normalProfile.general - 4, calibratedProfile.general)
        assertEquals(normalProfile.redDot - 3, calibratedProfile.redDot)
    }

    @Test
    fun testOptimizerRecommendations() {
        val lowEnd = DeviceHardware(totalRamGb = 2f, assignedTier = DeviceTier.LOW_END)
        val flagship = DeviceHardware(totalRamGb = 12f, currentRefreshRate = 120f, assignedTier = DeviceTier.FLAGSHIP)

        val lowOpt = SensitivityEngine.optimizeSettings(lowEnd)
        val flagOpt = SensitivityEngine.optimizeSettings(flagship)

        assertEquals("Smooth", lowOpt.recommendedGraphics)
        assertEquals("Ultra", flagOpt.recommendedGraphics)
        assertTrue(lowOpt.performanceNotes.isNotEmpty())
    }
}
