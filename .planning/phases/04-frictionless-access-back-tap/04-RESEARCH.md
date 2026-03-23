# Phase 04: Frictionless Access (Back Tap) - Research

**Researched:** March 23, 2026
**Domain:** Android physical gesture detection & Accessibility Services
**Confidence:** HIGH

## Summary
Phase 4 focuses on implementing a custom `AccessibilityService` to detect physical "Back Tap" gestures (double-tap on the back of the device) and providing a one-time onboarding guide. Research confirms that an `AccessibilityService` is the optimal host for background sensor monitoring as it possesses the necessary privileges to launch activities from the background on modern Android versions (10+).

**Primary recommendation:** Use the `Accelerometer` sensor (specifically the Z-axis) to detect high-magnitude spikes indicative of a physical tap, and gate this monitoring with `DisplayManager` to preserve battery when the screen is off.

## User Constraints (from CONTEXT.md)
### Locked Decisions
- Use custom Accessibility Service for gesture detection.
- Focus on "double-tap" (accelerometer-based) detection.
- Destination: `CurrentTasksView`.
- Onboarding: One-time walkthrough during setup.
- No UI status indicator for the gesture.

## Standard Stack
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Android SDK | API 34+ | Accessibility & Sensors | Platform native |
| Jetpack DataStore | 1.1.0 | Persistence | Modern preferences standard |
| Jetpack Compose | 1.7.0 | UI | Project standard |

## Architecture Patterns
### Background Gesture Detection
1. **The Monitor:** An `AccessibilityService` that runs continuously.
2. **The Sensor:** Implementation of `SensorEventListener` to process accelerometer data.
3. **The Gating:** Implementation of `DisplayManager.DisplayListener` to only register sensors when `Display.STATE_ON`.
4. **The Trigger:** Detection of two peaks > `TAP_THRESHOLD` (approx 15.0 m/s²) within a 200-500ms window.

## Deep Linking & Background Launch
Android 14+ requires explicit `ActivityOptions` for background launches.
```kotlin
val options = ActivityOptions.makeBasic()
    .setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
val intent = Intent(this, MainActivity::class.java).apply {
    action = MainActivity.ACTION_VIEW_TASKS
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
}
startActivity(intent, options.toBundle())
```

## Onboarding Persistence
Use `PreferenceDataStore` to store a boolean `onboarding_completed`.
- Check flag in `SetupFlow`.
- If false, show `BackTapOnboarding` walkthrough.
- On dismiss, set flag to true.

## Common Pitfalls
- **False Positives:** Table bumps or vigorous walking can trigger detection. Use a strict time window and Z-axis orientation check.
- **Battery Drain:** Continuous sensor listening is expensive. **MANDATORY**: Unregister the listener when the display is off.
- **Service Death:** System might kill the service. Set `android:canRetrieveWindowContent="false"` and `android:accessibilityEventTypes=""` in the XML config to keep the footprint small.

## Environment Availability
- Accelerometer is standard on almost all Android devices.
- `AccessibilityService` requires manual user enablement in system settings.

## Validation Architecture
- **Wave 0 (Connected Tests):** `BackTapTest.kt` using `AccessibilityServiceConnection` to verify intent delivery.
- **Manual Checklist:** See `04-VALIDATION.md`.
