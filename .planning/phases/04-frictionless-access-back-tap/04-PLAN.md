# Phase 4 Plan — Frictionless Access (Back Tap)

---
phase: 04-frictionless-access-back-tap
plan: 01
type: execute
wave: 1
depends_on: []
files_modified: [app/src/main/AndroidManifest.xml, app/src/main/res/xml/accessibility_service_config.xml, app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt]
autonomous: true
requirements: [GEST-01]

must_haves:
  truths:
    - "ZenStackAccessibilityService is registered in the system settings"
    - "Double-tap trigger logic successfully fires a launch intent to MainActivity"
  artifacts:
    - path: "app/src/main/res/xml/accessibility_service_config.xml"
      provides: "Accessibility service configuration"
    - path: "app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt"
      provides: "Accessibility Service skeleton and launch logic"
  key_links:
    - from: "ZenStackAccessibilityService"
      to: "MainActivity"
      via: "Intent(ACTION_VIEW_TASKS)"
---

<objective>
Establish the infrastructure for the Back Tap Accessibility Service. This plan creates the service skeleton and registers it in the system to enable background activity launching.

Purpose: Bypassing background launch restrictions (Android 10+) using the privileged Accessibility Service.
Output: Registered Accessibility Service capable of launching the app.
</objective>

<execution_context>
@M:/programs/organizationTool/.gemini/get-shit-done/workflows/execute-plan.md
</execution_context>

<context>
@.planning/PROJECT.md
@.planning/ROADMAP.md
@.planning/phases/04-frictionless-access-back-tap/04-CONTEXT.md
@.planning/phases/04-frictionless-access-back-tap/04-RESEARCH.md
@app/src/main/AndroidManifest.xml
@app/src/main/java/hr/fipu/organizationtool/MainActivity.kt
</context>

<tasks>

<task type="auto">
  <name>Task 1: Create accessibility service configuration</name>
  <files>app/src/main/res/xml/accessibility_service_config.xml</files>
  <action>
    Create the XML configuration for the accessibility service.
    - Set `accessibilityEventTypes="typeAllMask"`.
    - Set `accessibilityFeedbackType="feedbackGeneric"`.
    - Set `canRetrieveWindowContent="false"` (for privacy/efficiency).
    - Set `canPerformGestures="true"`.
    - Reference per-D-01 research: `android:description="@string/accessibility_description"`.
  </action>
  <verify>
    <automated>test -f app/src/main/res/xml/accessibility_service_config.xml</automated>
  </verify>
  <done>XML configuration file created with correct parameters.</done>
</task>

<task type="auto">
  <name>Task 2: Implement ZenStackAccessibilityService skeleton</name>
  <files>app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</files>
  <action>
    Create `ZenStackAccessibilityService` extending `AccessibilityService`.
    - Implement `onAccessibilityEvent` and `onInterrupt` as stubs.
    - Add `triggerLaunch()` method that builds an intent for `MainActivity.ACTION_VIEW_TASKS`.
    - Use `Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP`.
    - Per RESEARCH.md: If API 34+, use `ActivityOptions.setPendingIntentBackgroundActivityStartMode(MODE_BACKGROUND_ACTIVITY_START_ALLOWED)`.
  </action>
  <verify>
    <automated>grep -q "ZenStackAccessibilityService" app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</automated>
  </verify>
  <done>Service skeleton with launch logic exists.</done>
</task>

<task type="auto">
  <name>Task 3: Register service in AndroidManifest.xml</name>
  <files>app/src/main/AndroidManifest.xml</files>
  <action>
    Add the `<service>` declaration to `AndroidManifest.xml`.
    - Set `android:name=".service.ZenStackAccessibilityService"`.
    - Set `android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"`.
    - Include `<intent-filter>` for `android.accessibilityservice.AccessibilityService`.
    - Add `<meta-data>` pointing to `@xml/accessibility_service_config`.
  </action>
  <verify>
    <automated>grep -q "android.permission.BIND_ACCESSIBILITY_SERVICE" app/src/main/AndroidManifest.xml</automated>
  </verify>
  <done>Service registered and visible to the Android system.</done>
</task>

<task type="checkpoint:human-verify" gate="blocking">
  <what-built>Registered Accessibility Service Infrastructure</what-built>
  <how-to-verify>
    1. Deploy the app to a device/emulator.
    2. Go to System Settings -> Accessibility -> Downloaded/Installed Services.
    3. Verify "ZenStack" appears in the list.
    4. Enable it (no behavior yet, just verify it stays enabled without crashing).
  </how-to-verify>
  <resume-signal>Service is visible and enable-able</resume-signal>
</task>

</tasks>

<success_criteria>
- Accessibility Service is registered in the Manifest.
- Config file exists.
- Service is visible in Android system settings.
</success_criteria>

<output>
After completion, create `.planning/phases/04-frictionless-access-back-tap/04-01-SUMMARY.md`
</output>

---
phase: 04-frictionless-access-back-tap
plan: 02
type: execute
wave: 2
depends_on: [04-01]
files_modified: [app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt]
autonomous: true
requirements: [GEST-01]

must_haves:
  truths:
    - "Physical double-tap on device back triggers app launch"
    - "Detection does not trigger on single taps or general motion"
  artifacts:
    - path: "app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt"
      provides: "Accelerometer processing logic"
  key_links:
    - from: "ZenStackAccessibilityService"
      to: "SensorManager"
      via: "SensorEventListener on Z-axis"
---

<objective>
Implement the core gesture detection logic using the device accelerometer. This plan transforms the service from a skeleton into an active gesture listener.

Purpose: Detecting physical "Back Tap" gestures without using private APIs.
Output: Functional gesture detection that launches the app.
</objective>

<execution_context>
@M:/programs/organizationTool/.gemini/get-shit-done/workflows/execute-plan.md
</execution_context>

<context>
@.planning/phases/04-frictionless-access-back-tap/04-RESEARCH.md
@app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt
</context>

<tasks>

<task type="auto">
  <name>Task 1: Implement SensorEventListener for peak detection</name>
  <files>app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</files>
  <action>
    Implement `SensorEventListener` in `ZenStackAccessibilityService`.
    - Obtain `SensorManager` and register for `Sensor.TYPE_ACCELEROMETER` with `SENSOR_DELAY_UI`.
    - In `onSensorChanged`, monitor the Z-axis (`event.values[2]`).
    - Implement the peak detection algorithm from RESEARCH.md:
      - `TAP_THRESHOLD` (approx 15.0f).
      - `WINDOW_START` (150ms debounce).
      - `WINDOW_END` (500ms max for 2nd tap).
    - Call `triggerLaunch()` when double-tap detected.
  </action>
  <verify>
    <automated>grep -q "SensorEventListener" app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</automated>
  </verify>
  <done>Accelerometer data is processed for double-tap patterns.</done>
</task>

<task type="auto">
  <name>Task 2: Implement power-aware listening</name>
  <files>app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</files>
  <action>
    Optimize battery usage per RESEARCH.md recommendations.
    - Only listen to accelerometer when the screen is ON.
    - Use `onServiceConnected` and `onUnbind` to manage sensor registration.
    - Register a `BroadcastReceiver` for `ACTION_SCREEN_ON` and `ACTION_SCREEN_OFF` if necessary, or check `DisplayManager`.
  </action>
  <verify>
    <automated>grep -q "onServiceConnected" app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</automated>
  </verify>
  <done>Service listens only when device is active.</done>
</task>

<task type="checkpoint:human-verify" gate="blocking">
  <what-built>Gesture Detection Logic</what-built>
  <how-to-verify>
    1. Deploy to a physical device (Emulators may lack back-tap simulation unless using sensor injection).
    2. Enable "ZenStack" Accessibility Service.
    3. Exit the app.
    4. Double-tap the back of the phone firmly.
    5. Verify ZenStack launches to `CurrentTasksView`.
    6. Verify single taps or normal shaking do NOT launch the app.
  </how-to-verify>
  <resume-signal>Gesture detection works as expected</resume-signal>
</task>

</tasks>

<success_criteria>
- App launches upon physical double-tap.
- Low false-positive rate during normal usage.
- Service is battery-conscious.
</success_criteria>

<output>
After completion, create `.planning/phases/04-frictionless-access-back-tap/04-02-SUMMARY.md`
</output>

---
phase: 04-frictionless-access-back-tap
plan: 03
type: execute
wave: 1
depends_on: []
files_modified: [gradle/libs.versions.toml, app/build.gradle.kts, app/src/main/java/hr/fipu/organizationtool/data/OnboardingRepository.kt, app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt, app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt]
autonomous: true
requirements: [GEST-02]

must_haves:
  truths:
    - "New users see the 'How to enable Back Tap' guide during setup"
    - "The guide is hidden after completion or on subsequent app launches"
  artifacts:
    - path: "app/src/main/java/hr/fipu/organizationtool/data/OnboardingRepository.kt"
      provides: "Persistence for walkthrough status"
    - path: "app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt"
      provides: "Walkthrough UI"
  key_links:
    - from: "SetupFlow"
      to: "OnboardingRepository"
      via: "Boolean check for hasSeenBackTapGuide"
---

<objective>
Implement the one-time onboarding guide for the Back Tap feature. This ensures users know how to activate the accessibility service they just built.

Purpose: Educational onboarding for the gesture feature.
Output: One-time walkthrough UI integrated into the setup flow.
</objective>

<execution_context>
@M:/programs/organizationTool/.gemini/get-shit-done/workflows/execute-plan.md
</execution_context>

<context>
@.planning/phases/04-frictionless-access-back-tap/04-CONTEXT.md
@app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt
@gradle/libs.versions.toml
</context>

<tasks>

<task type="auto">
  <name>Task 1: Setup DataStore and OnboardingRepository</name>
  <files>gradle/libs.versions.toml, app/build.gradle.kts, app/src/main/java/hr/fipu/organizationtool/data/OnboardingRepository.kt</files>
  <action>
    - Add `androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version = "1.1.2" }` to `libs.versions.toml`.
    - Add dependency to `app/build.gradle.kts`.
    - Create `OnboardingRepository` using DataStore.
    - Provide a Flow `hasSeenBackTapGuide: Flow<Boolean>` and a function `markBackTapGuideAsSeen()`.
    - Register repository in Koin module (if existing) or instantiate in ViewModel.
  </action>
  <verify>
    <automated>grep -q "datastore-preferences" gradle/libs.versions.toml</automated>
  </verify>
  <done>Persistence layer for onboarding state is ready.</done>
</task>

<task type="auto">
  <name>Task 2: Create BackTapOnboarding UI and integrate into SetupFlow</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt, app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</files>
  <action>
    - Create `BackTapOnboarding` component using `HorizontalPager` or simple `Steps` UI.
    - Content: Instructions for System Settings -> Accessibility -> ZenStack.
    - Modify `SetupFlow` in `ZenStackApp.kt`:
      - Fetch `hasSeenBackTapGuide` from repository/viewModel.
      - Add a 4th step `BackTapOnboardingStep` to `SetupFlow` that appears only if `hasSeenBackTapGuide` is false.
      - Call `markBackTapGuideAsSeen()` when user confirms or finishes setup.
  </action>
  <verify>
    <automated>grep -q "BackTapOnboarding" app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</automated>
  </verify>
  <done>Walkthrough is integrated into the setup experience.</done>
</task>

<task type="checkpoint:human-verify" gate="blocking">
  <what-built>One-Time Onboarding Experience</what-built>
  <how-to-verify>
    1. Clear App Data.
    2. Launch ZenStack.
    3. Complete Brain Dump and Power 3 steps.
    4. Verify the "Back Tap Guide" appears as the final step.
    5. Complete setup.
    6. Restart app or reset session.
    7. Verify the "Back Tap Guide" does NOT appear again.
  </how-to-verify>
  <resume-signal>Onboarding shows once as expected</resume-signal>
</task>

</tasks>

<success_criteria>
- DataStore correctly persists walkthrough status.
- UI guide shows instructions for Back Tap.
- Guide is strictly one-time.
</success_criteria>

<output>
After completion, create `.planning/phases/04-frictionless-access-back-tap/04-03-SUMMARY.md`
</output>
