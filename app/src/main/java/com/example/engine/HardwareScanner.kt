package com.example.engine

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.data.model.DeviceHardware
import com.example.data.model.DeviceTier
import java.io.File
import java.io.FileFilter
import java.util.regex.Pattern
import kotlin.math.hypot
import kotlin.math.roundToInt

object HardwareScanner {

    fun scan(context: Context): DeviceHardware {
        return try {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

            // RAM Specs
            var totalRam = 0f
            var availRam = 0f
            if (am != null) {
                try {
                    val memInfo = ActivityManager.MemoryInfo()
                    am.getMemoryInfo(memInfo)
                    totalRam = (memInfo.totalMem / (1024f * 1024f * 1024f) * 10).roundToInt() / 10f
                    availRam = (memInfo.availMem / (1024f * 1024f * 1024f) * 10).roundToInt() / 10f
                } catch (_: Exception) { }
            }

            // Storage
            val storage = getStorageInfo()

            // Display Specs
            var width = 1080
            var height = 2400
            var dpi = 400
            var diagonal = "Not available"
            var currentHz = 60f
            var supportedHz: List<Float> = listOf(60f)

            if (wm != null) {
                try {
                    val dm = DisplayMetrics()
                    @Suppress("DEPRECATION")
                    val display = wm.defaultDisplay
                    if (display != null) {
                        @Suppress("DEPRECATION")
                        display.getRealMetrics(dm)
                        width = if (dm.widthPixels > 0) dm.widthPixels else 1080
                        height = if (dm.heightPixels > 0) dm.heightPixels else 2400
                        dpi = if (dm.densityDpi > 0) dm.densityDpi else 400
                        diagonal = calculateScreenInches(dm, width, height)
                        currentHz = if (display.refreshRate > 0) display.refreshRate else 60f
                        supportedHz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            display.supportedModes?.map { it.refreshRate }?.distinct() ?: listOf(currentHz)
                        } else {
                            listOf(currentHz)
                        }
                    }
                } catch (_: Exception) { }
            }

            // Battery & Thermal
            val battery = getBatteryInfo(context)
            val thermal = getThermalState(context)

            // GPU & OpenGL
            val openGl = try {
                am?.deviceConfigurationInfo?.glEsVersion ?: "Not available"
            } catch (_: Exception) {
                "Not available"
            }
            val gpuRenderer = queryHeadlessGpuRenderer()

            val cores = getCpuCoreCount()
            val processor = detectProcessor()

            val calculatedTier = when {
                totalRam >= 11.5f || (totalRam >= 7.5f && currentHz >= 120f) -> DeviceTier.FLAGSHIP
                totalRam >= 7.5f || (totalRam >= 5.5f && currentHz >= 90f) -> DeviceTier.HIGH_END
                totalRam >= 5.5f -> DeviceTier.MID_RANGE
                totalRam >= 3.5f -> DeviceTier.BUDGET
                totalRam > 0f -> DeviceTier.LOW_END
                else -> DeviceTier.MID_RANGE
            }

            DeviceHardware(
                brand = if (!Build.BRAND.isNullOrBlank() && Build.BRAND != "unknown") Build.BRAND.replaceFirstChar { it.uppercase() } else "Not available",
                model = if (!Build.MODEL.isNullOrBlank() && Build.MODEL != "unknown") Build.MODEL else "Not available",
                manufacturer = if (!Build.MANUFACTURER.isNullOrBlank() && Build.MANUFACTURER != "unknown") Build.MANUFACTURER.replaceFirstChar { it.uppercase() } else "Not available",
                processor = processor,
                cpuCores = cores,
                cpuArchitecture = Build.SUPPORTED_ABIS?.firstOrNull() ?: "Not available",
                gpuRenderer = gpuRenderer,
                totalRamGb = totalRam,
                availableRamGb = availRam,
                totalStorageGb = storage.first,
                availableStorageGb = storage.second,
                androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                screenResolution = "${width} x ${height} Px",
                screenDiagonalInches = diagonal,
                screenDensityDpi = dpi,
                currentRefreshRate = currentHz,
                supportedRefreshRates = supportedHz,
                batteryPct = battery.first,
                batteryTempC = battery.second,
                thermalStatus = thermal,
                openGlEsVersion = openGl,
                touchSamplingRate = "Not available", // Android public SDK does not expose touch polling rate
                assignedTier = calculatedTier
            )
        } catch (_: Exception) {
            DeviceHardware()
        }
    }

    private fun detectProcessor(): String {
        return try {
            if (!Build.HARDWARE.isNullOrBlank() && Build.HARDWARE != "unknown") {
                Build.HARDWARE.uppercase()
            } else if (!Build.BOARD.isNullOrBlank() && Build.BOARD != "unknown") {
                Build.BOARD.uppercase()
            } else {
                "Not available"
            }
        } catch (_: Exception) {
            "Not available"
        }
    }

    private fun getStorageInfo(): Pair<Float, Float> {
        return try {
            val path = Environment.getDataDirectory()?.path ?: return Pair(0f, 0f)
            val stat = StatFs(path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availBlocks = stat.availableBlocksLong

            val total = ((totalBlocks * blockSize) / (1024f * 1024f * 1024f) * 10).roundToInt() / 10f
            val avail = ((availBlocks * blockSize) / (1024f * 1024f * 1024f) * 10).roundToInt() / 10f
            Pair(total, avail)
        } catch (_: Exception) {
            Pair(0f, 0f)
        }
    }

    private fun calculateScreenInches(dm: DisplayMetrics, width: Int, height: Int): String {
        return try {
            if (dm.xdpi > 0 && dm.ydpi > 0) {
                val xInches = width / dm.xdpi
                val yInches = height / dm.ydpi
                val diag = hypot(xInches.toDouble(), yInches.toDouble())
                "${(diag * 10).roundToInt() / 10.0}\""
            } else {
                "Not available"
            }
        } catch (_: Exception) {
            "Not available"
        }
    }

    private fun getBatteryInfo(context: Context): Pair<Int, Float> {
        return try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val intent = context.registerReceiver(null, filter)
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val pct = if (level >= 0 && scale > 0) (level * 100) / scale else -1
            val tempRaw = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
            val tempC = if (tempRaw > 0) tempRaw / 10f else -1f
            Pair(pct, tempC)
        } catch (_: Exception) {
            Pair(-1, -1f)
        }
    }

    private fun getThermalState(context: Context): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                when (pm?.currentThermalStatus) {
                    PowerManager.THERMAL_STATUS_NONE -> "Nominal (Cool)"
                    PowerManager.THERMAL_STATUS_LIGHT -> "Light Warmth"
                    PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Heat"
                    PowerManager.THERMAL_STATUS_SEVERE -> "Thermal Throttling Risk"
                    PowerManager.THERMAL_STATUS_CRITICAL -> "Thermal Critical"
                    PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency Shutdown"
                    PowerManager.THERMAL_STATUS_SHUTDOWN -> "Shutting Down"
                    else -> "Not available"
                }
            } else {
                "Not available"
            }
        } catch (_: Exception) {
            "Not available"
        }
    }

    private fun getCpuCoreCount(): Int {
        return try {
            val dir = File("/sys/devices/system/cpu/")
            val files = dir.listFiles(FileFilter { Pattern.matches("cpu[0-9]+", it.name) })
            files?.size ?: Runtime.getRuntime().availableProcessors()
        } catch (_: Exception) {
            Runtime.getRuntime().availableProcessors()
        }
    }

    private fun queryHeadlessGpuRenderer(): String {
        var dpy: EGLDisplay = EGL14.EGL_NO_DISPLAY
        var ctx: EGLContext = EGL14.EGL_NO_CONTEXT
        var surf: EGLSurface = EGL14.EGL_NO_SURFACE
        return try {
            dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            if (dpy == EGL14.EGL_NO_DISPLAY) return "Not available"
            val vers = IntArray(2)
            if (!EGL14.eglInitialize(dpy, vers, 0, vers, 1)) return "Not available"

            val configAttr = intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfig = IntArray(1)
            EGL14.eglChooseConfig(dpy, configAttr, 0, configs, 0, 1, numConfig, 0)
            if (numConfig[0] == 0 || configs[0] == null) return "Not available"

            val ctxAttr = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
            ctx = EGL14.eglCreateContext(dpy, configs[0], EGL14.EGL_NO_CONTEXT, ctxAttr, 0)
            if (ctx == EGL14.EGL_NO_CONTEXT) return "Not available"

            val surfAttr = intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE)
            surf = EGL14.eglCreatePbufferSurface(dpy, configs[0], surfAttr, 0)
            if (surf == EGL14.EGL_NO_SURFACE) return "Not available"

            EGL14.eglMakeCurrent(dpy, surf, surf, ctx)
            val renderer = GLES20.glGetString(GLES20.GL_RENDERER)
            renderer ?: "Not available"
        } catch (_: Exception) {
            "Not available"
        } finally {
            try {
                if (dpy != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
                    if (surf != EGL14.EGL_NO_SURFACE) EGL14.eglDestroySurface(dpy, surf)
                    if (ctx != EGL14.EGL_NO_CONTEXT) EGL14.eglDestroyContext(dpy, ctx)
                    EGL14.eglTerminate(dpy)
                }
            } catch (_: Exception) { }
        }
    }
}
