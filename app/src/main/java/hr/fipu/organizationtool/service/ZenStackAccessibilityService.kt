package hr.fipu.organizationtool.service

import android.accessibilityservice.AccessibilityService
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.DisplayManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import hr.fipu.organizationtool.MainActivity

class ZenStackAccessibilityService : AccessibilityService(), SensorEventListener, DisplayManager.DisplayListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var displayManager: DisplayManager
    private var accelerometer: Sensor? = null

    private var lastTapTime: Long = 0
    private val tapThreshold = 15.0f
    private val tapWindowMin = 150L
    private val tapWindowMax = 500L

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
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val z = event.values[2]
            if (z > tapThreshold) {
                val now = System.currentTimeMillis()
                if (now - lastTapTime in tapWindowMin..tapWindowMax) {
                    Log.d("ZenStack", "Double-tap detected on Z-axis ($z)")
                    launchMainActivity()
                    lastTapTime = 0 // Reset to avoid triple-tap triggering twice
                } else {
                    lastTapTime = now
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
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
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
