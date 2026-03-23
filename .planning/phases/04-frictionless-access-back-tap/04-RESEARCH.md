# Phase 4: Frictionless Access (Back Tap) - Research

**Researched:** March 23, 2026
**Domain:** Android Accessibility Services & Sensor Fusion
**Confidence:** HIGH

## Summary

This phase implements a "zero-click" launch mechanism for ZenStack using a physical "Back Tap" gesture. Research confirms that while Android does not provide a public "Back Tap" API for third-party apps (features like "Quick Tap" are system-exclusive), it can be reliably implemented by combining an **Accessibility Service** with **Accelerometer sensor monitoring**. 

The `AccessibilityService` is the ideal host because it persists in the background more reliably than a standard Service and possesses the necessary privileges to launch activities from the background (avoiding Android 10+ background activity start restrictions). The gesture detection will use a threshold-based peak detection algorithm on the device's Z-axis acceleration.

**Primary recommendation:** Implement `ZenStackAccessibilityService` using `SensorManager` for double-tap detection and leverage `DataStore` for a one-time onboarding walkthrough integrated into the existing `SetupFlow`.

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- Use custom Accessibility Service for gesture detection.
- Focus on "double-tap" detection to launch the app.
- Destination: CurrentTasksView.
- Onboarding: One-time walkthrough during setup.
- No UI status indicator for the gesture.

### the agent's Discretion
- Implementation details of the detection algorithm (thresholds vs. ML).
- Integration method of the onboarding guide into the setup flow.

### Deferred Ideas (OUT OF SCOPE)
- Custom Gesture Configuration: Allowing users to map "Triple Tap" or other gestures.
- System Settings Deep Linking: Navigating users directly to specific OEM settings pages.
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| GEST-01 | Back Tap Gesture - Launch via double-tap. | Confirmed `AccessibilityService` + `SensorManager` is the standard way to implement this on Android. |
| GEST-02 | Onboarding Guide - In-app visual "How-To". | `DataStore` confirmed for persistence; `HorizontalPager` for the walkthrough UI. |
</phase_requirements>

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| `androidx.datastore:datastore-preferences` | 1.1.2 | Persistence | Modern, type-safe replacement for SharedPreferences. |
| `androidx.compose.foundation` | 1.7.0+ | UI (Pager) | Provides `HorizontalPager` for walkthroughs. |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|--------------|
| `SensorManager` | (Native) | Gesture Detection | Required for reading accelerometer data. |

**Installation:**
```kotlin
// build.gradle.kts
dependencies {
    implementation("androidx.datastore:datastore-preferences:1.1.2")
}
```

## Architecture Patterns

### Recommended Project Structure
```
app/src/main/java/hr/fipu/organizationtool/
├── service/
│   └── ZenStackAccessibilityService.kt  # Gesture detection & launch logic
├── data/
│   └── OnboardingRepository.kt          # DataStore wrapper
└── ui/
    └── components/
        └── BackTapOnboarding.kt         # Walkthrough UI component
```

### Pattern 1: Double-Tap Peak Detection
**What:** A threshold-based algorithm that monitors the Z-axis of the accelerometer for two distinct "spikes" within a specific time window.
**When to use:** Detecting physical impacts (taps) on the device chassis.
**Example:**
```kotlin
// Source: Community Pattern for Simple Tap Detection
val TAP_THRESHOLD = 15.0f      // Adjust for sensitivity
val WINDOW_START = 150L        // Min time between taps (debounce)
val WINDOW_END = 500L          // Max time for second tap

override fun onSensorChanged(event: SensorEvent) {
    val z = event.values[2] // Z-axis is most sensitive for back taps
    if (Math.abs(z) > TAP_THRESHOLD) {
        val now = System.currentTimeMillis()
        if (now - lastTapTime in WINDOW_START..WINDOW_END) {
            triggerLaunch()
        }
        lastTapTime = now
    }
}
```

### Anti-Patterns to Avoid
- **Continuous Fast Sampling:** Do not use `SENSOR_DELAY_FASTEST` indefinitely. Use "Gates" (e.g., only listen when the screen is ON) to save battery.
- **Manual String Matching for Settings:** Avoid hardcoding paths to system accessibility settings, as they vary by OEM. Provide a general guide instead.

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Complex Tap ML | Custom TensorFlow Model | Simple Threshold | ML is overkill for v1 and increases APK size significantly. |
| Screen-Off Detection | Custom Power State Logic | `DisplayManager` / `PowerManager` | System APIs are more reliable for battery-conscious logic. |

## Common Pitfalls

### Pitfall 1: Background Activity Restrictions
**What goes wrong:** `startActivity` fails or does nothing when called from the background on Android 10+.
**How to avoid:** Use `Intent.FLAG_ACTIVITY_NEW_TASK`. On Android 14+, explicitly allow background starts via `ActivityOptions.setPendingIntentBackgroundActivityStartMode`.
**Warning signs:** Logcat shows "Background activity start from [package] blocked".

### Pitfall 2: False Positives (The "Table Bump")
**What goes wrong:** Setting the phone on a table or walking triggers the app launch.
**How to avoid:** Use a "Cooldown" period after a detection and ensure the threshold is high enough. Optionally, ignore detection if the proximity sensor indicates the phone is in a pocket.

## Code Examples

### Reliable Activity Launch (Android 14 Compatible)
```kotlin
// Source: https://developer.android.com/guide/components/activities/background-starts
val intent = Intent(this, MainActivity::class.java).apply {
    action = MainActivity.ACTION_VIEW_TASKS
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
}

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
    val options = ActivityOptions.makeBasic()
    options.setPendingIntentBackgroundActivityStartMode(
        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
    )
    startActivity(intent, options.toBundle())
} else {
    startActivity(intent)
}
```

### Accessibility Configuration (xml/accessibility_service_config.xml)
```xml
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/accessibility_description"
    android:accessibilityEventTypes="typeAllMask"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:notificationTimeout="100"
    android:canRetrieveWindowContent="false"
    android:canPerformGestures="true"
    android:accessibilityFlags="flagDefault" />
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| `SharedPreferences` | `DataStore` | 2021 | Type-safety and Coroutine support. |
| Standard `Service` | `AccessibilityService` | Android 10+ | Bypasses background launch restrictions. |

## Open Questions

1. **Battery Consumption**
   - What we know: Accelerometer use in background impacts battery.
   - What's unclear: The exact drain on modern devices with "low-power" sensor hubs.
   - Recommendation: Default to listening only when the screen is ON to minimize impact.

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| `SensorManager` | GEST-01 | ✓ | Native | — |
| `DataStore` | GEST-02 | ✗ | — | Add dependency to `libs.versions.toml` |
| `HorizontalPager` | GEST-02 | ✓ | (Compose) | — |

**Missing dependencies with no fallback:**
- None.

**Missing dependencies with fallback:**
- `DataStore`: Can fallback to `SharedPreferences` if build issues arise, but `DataStore` is preferred.

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 4 + Espresso + Compose Test |
| Config file | `app/src/androidTest/` |
| Quick run command | `./gradlew test` |
| Full suite command | `./gradlew connectedAndroidTest` |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| GEST-01 | Double-tap launches app | Integration | `connectedAndroidTest` | ❌ Wave 0 |
| GEST-02 | Onboarding shows only once | UI / Logic | `connectedAndroidTest` | ❌ Wave 0 |

## Sources

### Primary (HIGH confidence)
- Official Android Docs - Accessibility Service & Background Activity Launches.
- Android Developer - Sensors Overview.
- Jetpack DataStore Guide.

### Secondary (MEDIUM confidence)
- "Tap, Tap" Open Source project (implementation patterns).
- Community algorithms for threshold-based tap detection.

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Libraries are stable and standard.
- Architecture: HIGH - Accessibility Service + Sensors is a proven pattern for this feature.
- Pitfalls: MEDIUM - False positives are hardware-dependent and require tuning.

**Research date:** March 23, 2026
**Valid until:** April 22, 2026
