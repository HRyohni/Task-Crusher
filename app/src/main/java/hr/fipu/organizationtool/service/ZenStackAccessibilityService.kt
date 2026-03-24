package hr.fipu.organizationtool.service

import android.accessibilityservice.AccessibilityService
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import hr.fipu.organizationtool.MainActivity

class ZenStackAccessibilityService : AccessibilityService(), SensorEventListener, DisplayManager.DisplayListener {

    companion object {
        private const val LOWPASS_ALPHA = 0.2f
        private const val HIGHPASS_ALPHA = 0.2f
        private const val MIN_NOISE = 0.05f
        private const val PEAK_WINDOW = 64
        private const val TAP_MIN_GAP_MS = 100L
        private const val TAP_MAX_GAP_MS = 500L
        private const val GESTURE_THROTTLE_MS = 500L
        private const val PEAK_RATIO_MAX = 3.0f
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var displayManager: DisplayManager
    private var accelerometer: Sensor? = null

    // Signal chain state
    private var prevZ = 0f
    private var lpZ = 0f
    private var hpZ = 0f
    private var prevLpZ = 0f

    // Peak tracking
    private var posPeak = 0f
    private var negPeak = 0f
    private var sampleCount = 0

    // Timing
    private val tapTimestamps = ArrayDeque<Long>()
    private var lastGestureTime = 0L

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        displayManager.registerDisplayListener(this, null)
        updateSensorRegistration()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed for gesture detection
    }

    override fun onInterrupt() {
        // Not needed
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return
        processSample(event.values[2])
    }

    private fun processSample(z: Float) {
        // 1. Slope (first difference)
        val slope = z - prevZ
        prevZ = z

        // 2. Low-pass filter
        lpZ = lpZ + LOWPASS_ALPHA * (slope - lpZ)

        // 3. High-pass filter
        hpZ = lpZ - prevLpZ
        prevLpZ = lpZ

        // 4. Peak tracking with PEAK_WINDOW decay
        sampleCount++
        if (hpZ > 0f) {
            if (hpZ > posPeak) posPeak = hpZ
        } else {
            if (hpZ < negPeak) negPeak = hpZ
        }
        if (sampleCount >= PEAK_WINDOW) {
            posPeak *= 0.5f
            negPeak *= 0.5f
            sampleCount = 0
        }

        // 5. Adaptive noise gate
        val noiseThreshold = maxOf(MIN_NOISE, posPeak / 5f)
        if (kotlin.math.abs(hpZ) < noiseThreshold) return

        // 6. New negative peak? Check ratio
        if (hpZ < 0f && hpZ == negPeak && posPeak > 0f) {
            val ratio = kotlin.math.abs(negPeak) / posPeak
            if (ratio <= 0f || ratio >= PEAK_RATIO_MAX) return

            val now = System.currentTimeMillis()

            // 7. Purge stale timestamps
            while (tapTimestamps.isNotEmpty() && now - tapTimestamps.first() > TAP_MAX_GAP_MS) {
                tapTimestamps.removeFirst()
            }

            // 8. Enforce minimum gap
            if (tapTimestamps.isNotEmpty() && now - tapTimestamps.last() < TAP_MIN_GAP_MS) return

            tapTimestamps.addLast(now)

            // 9. Double-tap check
            if (tapTimestamps.size >= 2) {
                val span = tapTimestamps.last() - tapTimestamps.first()
                if (span in TAP_MIN_GAP_MS..TAP_MAX_GAP_MS) {
                    if (now - lastGestureTime >= GESTURE_THROTTLE_MS) {
                        lastGestureTime = now
                        tapTimestamps.clear()
                        Log.d("ZenStack", "Back double-tap detected (ratio=${"%.2f".format(ratio)}, span=${span}ms)")
                        launchMainActivity()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    private fun launchMainActivity() {
        val options = ActivityOptions.makeBasic()
            .setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)

        val intent = Intent(this, MainActivity::class.java).apply {
            action = MainActivity.ACTION_VIEW_TASKS
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        try {
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e("ZenStack", "Failed to launch MainActivity from background", e)
        }
    }

    private fun updateSensorRegistration() {
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        if (display?.state == Display.STATE_ON) {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        } else {
            sensorManager.unregisterListener(this)
        }
    }

    // DisplayListener implementation
    override fun onDisplayAdded(displayId: Int) {}
    override fun onDisplayRemoved(displayId: Int) {}
    override fun onDisplayChanged(displayId: Int) {
        if (displayId == Display.DEFAULT_DISPLAY) {
            updateSensorRegistration()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        displayManager.unregisterDisplayListener(this)
        sensorManager.unregisterListener(this)
    }
}
