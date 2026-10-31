package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.HardwareScanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun testAppNameString() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MX SENSE AI", appName)
    }

    @Test
    fun testHardwareScannerExecution() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val specs = HardwareScanner.scan(context)
        assertTrue(specs.totalRamGb >= 0f)
        assertTrue(specs.currentRefreshRate >= 0f)
    }
}
