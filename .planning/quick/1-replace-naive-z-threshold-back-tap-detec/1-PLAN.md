---
phase: quick-1
plan: 01
type: execute
wave: 1
depends_on: []
files_modified:
  - app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt
autonomous: true

must_haves:
  truths:
    - "A deliberate double-tap on the phone back fires the app launch intent"
    - "Incidental single taps and random vibration do not trigger the gesture"
    - "The sensor runs at SENSOR_DELAY_GAME to capture the signal with enough fidelity"
  artifacts:
    - path: "app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt"
      provides: "TapTap heuristic signal-processing pipeline replacing naive Z threshold"
      contains: "LOWPASS_ALPHA"
  key_links:
    - from: "onSensorChanged"
      to: "launchMainActivity"
      via: "processSample -> peak detection -> timing deque -> gesture throttle"
      pattern: "tapTimestamps\\.size >= 2"
---

<objective>
Replace the naive Z-axis threshold detector in ZenStackAccessibilityService with the TapTap heuristic signal-processing algorithm (ported from Google Columbus / KieronQuinn/TapTap).

Purpose: The naive detector fires on any single hard jolt with Z > 15.0f and misses real back-taps that produce a softer impulse. The heuristic approach filters the raw signal through slope, low-pass, and high-pass stages then validates the tap via peak ratio and timing deque, dramatically reducing false positives and false negatives.

Output: A single updated Kotlin file with the new algorithm. No new dependencies required.
</objective>

<execution_context>
@C:/Users/Kulen/.claude/get-shit-done/workflows/execute-plan.md
@C:/Users/Kulen/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/STATE.md
@app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt
</context>

<tasks>

<task type="auto">
  <name>Task 1: Implement TapTap heuristic pipeline in ZenStackAccessibilityService</name>
  <files>app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</files>
  <action>
Replace the entire body of ZenStackAccessibilityService.kt, preserving the existing imports, class declaration, DisplayManager.DisplayListener wiring, launchMainActivity(), and onDestroy() / onDisplayChanged() / onCreate() / onServiceConnected() lifecycle methods. Only the sensor-detection fields and onSensorChanged() are replaced.

**Remove these fields:**
- lastTapTime, tapThreshold, tapWindowMin, tapWindowMax

**Add companion object constants:**
```kotlin
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
```

**Add instance fields for signal state:**
```kotlin
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
```

**Replace onSensorChanged() with:**
```kotlin
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
```

**Change sensor registration rate** in updateSensorRegistration() from SENSOR_DELAY_NORMAL to SENSOR_DELAY_GAME:
```kotlin
sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
```

Add import at top if not present: `import kotlin.math.abs` (or use kotlin.math.abs inline as above — either is fine).

Do NOT change: class declaration, DisplayListener implementation, launchMainActivity(), onCreate(), onServiceConnected(), onDestroy(), onDisplayChanged(), onAccessibilityEvent(), onInterrupt(), onAccuracyChanged().
  </action>
  <verify>
    1. Open the file and confirm LOWPASS_ALPHA, HIGHPASS_ALPHA, processSample, tapTimestamps are present.
    2. Confirm SENSOR_DELAY_GAME is used in updateSensorRegistration().
    3. Confirm tapThreshold, tapWindowMin, tapWindowMax, lastTapTime fields are gone.
    4. Run: `./gradlew :app:compileDebugKotlin` — must complete with 0 errors.
  </verify>
  <done>
    File compiles cleanly. Old naive fields absent. Signal pipeline (slope → LP → HP → peak ratio → timing deque → throttle) present. Sensor registered at SENSOR_DELAY_GAME.
  </done>
</task>

</tasks>

<verification>
- `./gradlew :app:compileDebugKotlin` exits 0
- No references to tapThreshold or lastTapTime in the service file
- SENSOR_DELAY_GAME present in updateSensorRegistration
- processSample method exists and is called from onSensorChanged
</verification>

<success_criteria>
ZenStackAccessibilityService compiles and contains the full TapTap heuristic pipeline. The naive Z threshold detector is completely removed. Sensor runs at SENSOR_DELAY_GAME.
</success_criteria>

<output>
After completion, create `.planning/quick/1-replace-naive-z-threshold-back-tap-detec/1-SUMMARY.md` using the standard summary template.
</output>
