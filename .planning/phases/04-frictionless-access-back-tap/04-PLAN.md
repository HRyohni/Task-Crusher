# Phase 4 Plan — Frictionless Access (Back Tap)

---
phase: 04-frictionless-access-back-tap
plan: 00
type: execute
wave: 0
depends_on: []
files_modified: [app/src/androidTest/java/hr/fipu/organizationtool/BackTapTest.kt, app/src/androidTest/java/hr/fipu/organizationtool/OnboardingTest.kt]
autonomous: true
requirements: [GEST-01, GEST-02]

must_haves:
  truths:
    - "Integration test skeletons for Back Tap and Onboarding are created in Wave 0"
  artifacts:
    - path: "app/src/androidTest/java/hr/fipu/organizationtool/BackTapTest.kt"
      provides: "Back Tap gesture detection test skeleton"
    - path: "app/src/androidTest/java/hr/fipu/organizationtool/OnboardingTest.kt"
      provides: "One-time onboarding flow test skeleton"
---

<objective>
Create integration test skeletons to ensure TDD-friendly verification of the Back Tap and Onboarding features.

Purpose: Establishing verification gates before implementation.
Output: Test files ready for implementation.
</objective>

<execution_context>
@M:/programs/organizationTool/.gemini/get-shit-done/workflows/execute-plan.md
</execution_context>

<context>
@.planning/phases/04-frictionless-access-back-tap/04-VALIDATION.md
</context>

<tasks>

<task type="auto">
  <name>Task 1: Create BackTapTest skeleton</name>
  <files>app/src/androidTest/java/hr/fipu/organizationtool/BackTapTest.kt</files>
  <action>
    Create a skeleton integration test for the Back Tap gesture.
    - Setup a JUnit 4 test class.
    - Add a stub test `testDoubleTapLaunchesMainActivity`.
    - Per GEST-01: The test should eventually verify that the Accessibility Service triggers the correct intent.
  </action>
  <verify>
    <automated>test -f app/src/androidTest/java/hr/fipu/organizationtool/BackTapTest.kt</automated>
  </verify>
  <done>Test skeleton created.</done>
</task>

<task type="auto">
  <name>Task 2: Create OnboardingTest skeleton</name>
  <files>app/src/androidTest/java/hr/fipu/organizationtool/OnboardingTest.kt</files>
  <action>
    Create a skeleton integration test for the Onboarding guide.
    - Setup a JUnit 4 test class.
    - Add stub tests: `testOnboardingShowsOnFirstLaunch`, `testOnboardingHiddenAfterCompletion`.
    - Per GEST-02: The test should eventually verify DataStore persistence and UI visibility.
  </action>
  <verify>
    <automated>test -f app/src/androidTest/java/hr/fipu/organizationtool/OnboardingTest.kt</automated>
  </verify>
  <done>Test skeleton created.</done>
</task>

</tasks>

<success_criteria>
- Test files exist in the androidTest directory.
</success_criteria>

<output>
After completion, create `.planning/phases/04-frictionless-access-back-tap/04-00-SUMMARY.md`
</output>

---
phase: 04-frictionless-access-back-tap
plan: 01
type: execute
wave: 1
depends_on: [04-00]
files_modified: [app/src/main/java/hr/fipu/organizationtool/MainActivity.kt, gradle/libs.versions.toml, app/build.gradle.kts, app/src/main/java/hr/fipu/organizationtool/data/OnboardingRepository.kt]
autonomous: true
requirements: [GEST-01, GEST-02]

must_haves:
  truths:
    - "MainActivity uses ZenStackApp as its primary content"
    - "MainActivity identifies ACTION_VIEW_TASKS and prepares for navigation"
    - "OnboardingRepository is ready to persist the guide status"
  artifacts:
    - path: "app/src/main/java/hr/fipu/organizationtool/MainActivity.kt"
      provides: "Entry point with ZenStackApp integration"
    - path: "app/src/main/java/hr/fipu/organizationtool/data/OnboardingRepository.kt"
      provides: "DataStore-based persistence for onboarding"
  key_links:
    - from: "MainActivity"
      to: "ZenStackApp"
      via: "setContent"
    - from: "ZenStackApp"
      to: "OnboardingRepository"
      via: "State check for onboarding guide"
---

<objective>
Update the main entry point to use the production UI and establish the persistence layer for the onboarding guide.

Purpose: Fixing the target destination for GEST-01 and preparing GEST-02 infrastructure.
Output: Updated MainActivity and functional OnboardingRepository.
</objective>

<execution_context>
@M:/programs/organizationTool/.gemini/get-shit-done/workflows/execute-plan.md
</execution_context>

<context>
@app/src/main/java/hr/fipu/organizationtool/MainActivity.kt
@.planning/phases/04-frictionless-access-back-tap/04-CONTEXT.md
@.planning/phases/04-frictionless-access-back-tap/04-RESEARCH.md
</context>

<tasks>

<task type="auto">
  <name>Task 1: Update MainActivity to use ZenStackApp and handle ACTION_VIEW_TASKS</name>
  <files>app/src/main/java/hr/fipu/organizationtool/MainActivity.kt</files>
  <action>
    Update `MainActivity.kt`:
    - Replace `FoundationLab()` with `ZenStackApp()` in `setContent`.
    - Ensure `ACTION_VIEW_TASKS` is defined in the companion object (per current state).
    - Handle `ACTION_VIEW_TASKS` intent in `onCreate`. If detected, log and ensure the app is ready to display `CurrentTasksView` (ZenStackApp handles this reactive to data, so loading it is primary).
  </action>
  <verify>
    <automated>grep -q "ZenStackApp()" app/src/main/java/hr/fipu/organizationtool/MainActivity.kt</automated>
  </verify>
  <done>MainActivity integrated with production UI and intent constant defined/handled.</done>
</task>

<task type="auto">
  <name>Task 2: Setup DataStore and OnboardingRepository</name>
  <files>gradle/libs.versions.toml, app/build.gradle.kts, app/src/main/java/hr/fipu/organizationtool/data/OnboardingRepository.kt</files>
  <action>
    - Add `androidx-datastore-preferences` version `1.1.2` to `libs.versions.toml`.
    - Add dependency to `app/build.gradle.kts`.
    - Create `OnboardingRepository.kt` in `hr.fipu.organizationtool.data`.
    - Use `preferencesDataStore` to track `hasSeenBackTapGuide` (Boolean).
    - Expose `hasSeenBackTapGuide: Flow<Boolean>` and `suspend fun setHasSeenBackTapGuide(seen: Boolean)`.
  </action>
  <verify>
    <automated>test -f app/src/main/java/hr/fipu/organizationtool/data/OnboardingRepository.kt</automated>
  </verify>
  <done>Persistence layer for onboarding state is implemented.</done>
</task>

</tasks>

<success_criteria>
- MainActivity launches ZenStackApp.
- DataStore dependency is present and Repository is created.
</success_criteria>

<output>
After completion, create `.planning/phases/04-frictionless-access-back-tap/04-01-SUMMARY.md`
</output>

---
phase: 04-frictionless-access-back-tap
plan: 02
type: execute
wave: 1
depends_on: [04-00]
files_modified: [app/src/main/AndroidManifest.xml, app/src/main/res/xml/accessibility_service_config.xml, app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt]
autonomous: true
requirements: [GEST-01]

must_haves:
  truths:
    - "Accessibility Service is registered and visible in system settings"
    - "Service monitors accelerometer only when screen is ON using DisplayManager"
    - "Double-tap triggers ACTION_VIEW_TASKS intent to MainActivity"
  artifacts:
    - path: "app/src/main/res/xml/accessibility_service_config.xml"
      provides: "Accessibility service metadata"
    - path: "app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt"
      provides: "Power-optimized gesture detection"
  key_links:
    - from: "ZenStackAccessibilityService"
      to: "MainActivity"
      via: "Intent(ACTION_VIEW_TASKS)"
    - from: "ZenStackAccessibilityService"
      to: "DisplayManager"
      via: "DisplayListener for screen state"
---

<objective>
Implement the Accessibility Service with power-optimized gesture detection.

Purpose: Reliable background app launch via physical gesture (GEST-01).
Output: Functional, battery-conscious Accessibility Service.
</objective>

<execution_context>
@M:/programs/organizationTool/.gemini/get-shit-done/workflows/execute-plan.md
</execution_context>

<context>
@.planning/phases/04-frictionless-access-back-tap/04-RESEARCH.md
@app/src/main/AndroidManifest.xml
</context>

<tasks>

<task type="auto">
  <name>Task 1: Register Service and Create Configuration</name>
  <files>app/src/main/AndroidManifest.xml, app/src/main/res/xml/accessibility_service_config.xml</files>
  <action>
    - Create `app/src/main/res/xml/accessibility_service_config.xml` with `canPerformGestures="true"` and `feedbackGeneric`.
    - Add the `<service>` to `AndroidManifest.xml` with `BIND_ACCESSIBILITY_SERVICE` permission and the appropriate intent filter.
  </action>
  <verify>
    <automated>grep -q "BIND_ACCESSIBILITY_SERVICE" app/src/main/AndroidManifest.xml</automated>
  </verify>
  <done>Service infrastructure is registered in the system.</done>
</task>

<task type="auto">
  <name>Task 2: Implement ZenStackAccessibilityService with DisplayListener</name>
  <files>app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</files>
  <action>
    Implement `ZenStackAccessibilityService`:
    - Inherit `AccessibilityService`, `SensorEventListener`, and `DisplayManager.DisplayListener`.
    - In `onServiceConnected`, register `DisplayListener` with `DisplayManager`.
    - In `onDisplayChanged`, check `display.state`. If `Display.STATE_ON`, register the `Sensor.TYPE_ACCELEROMETER`. If `STATE_OFF`, unregister it.
    - Implement the peak detection algorithm (Z-axis, threshold 15.0f, window 150-500ms).
    - On detection, call `startActivity` with `ACTION_VIEW_TASKS`. Use `FLAG_ACTIVITY_NEW_TASK` and handle API 34+ background start restrictions as per RESEARCH.md.
  </action>
  <verify>
    <automated>grep -q "DisplayListener" app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt</automated>
  </verify>
  <done>Gesture detection logic is implemented with battery optimization.</done>
</task>

<task type="checkpoint:human-verify" gate="blocking">
  <what-built>Accessibility Service & Gesture Detection</what-built>
  <how-to-verify>
    1. Deploy to device. Enable ZenStack in Accessibility Settings.
    2. Exit app. Double-tap the back of the device.
    3. Verify ZenStack launches to the main screen.
    4. Verify it doesn't launch when screen is OFF (saving battery).
  </how-to-verify>
  <resume-signal>Service works and respects screen state</resume-signal>
</task>

</tasks>

<success_criteria>
- Service launches the app via double-tap.
- Sensor is inactive when screen is OFF.
</success_criteria>

<output>
After completion, create `.planning/phases/04-frictionless-access-back-tap/04-02-SUMMARY.md`
</output>

---
phase: 04-frictionless-access-back-tap
plan: 03
type: execute
wave: 2
depends_on: [04-01, 04-02]
files_modified: [app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt, app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt]
autonomous: true
requirements: [GEST-02]

must_haves:
  truths:
    - "The one-time onboarding guide is visible during setup"
    - "The legacy persistent 'How-to' section is removed from CurrentTasksView"
  artifacts:
    - path: "app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt"
      provides: "One-time walkthrough UI component"
  key_links:
    - from: "SetupFlow"
      to: "BackTapOnboarding"
      via: "Conditional step integration"
---

<objective>
Implement the one-time onboarding guide and clean up the UI by removing legacy components.

Purpose: Fulfilling GEST-02 and ensuring a clean, minimalist UI per CONTEXT.md.
Output: Integrated onboarding flow and streamlined main UI.
</objective>

<execution_context>
@M:/programs/organizationTool/.gemini/get-shit-done/workflows/execute-plan.md
</execution_context>

<context>
@app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt
@.planning/phases/04-frictionless-access-back-tap/04-CONTEXT.md
</context>

<tasks>

<task type="auto">
  <name>Task 1: Create BackTapOnboarding component</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt</files>
  <action>
    Create a Compose component `BackTapOnboarding` that provides a step-by-step visual guide for enabling the Accessibility Service.
    - Focus on minimalist design.
    - Instructions: Go to Settings -> Accessibility -> Downloaded Services -> ZenStack.
    - Include a "Got it" button that triggers completion.
  </action>
  <verify>
    <automated>test -f app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt</automated>
  </verify>
  <done>Onboarding UI component created.</done>
</task>

<task type="auto">
  <name>Task 2: Integrate Onboarding and Remove Legacy UI</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</files>
  <action>
    Update `ZenStackApp.kt`:
    - Integration: Add a step to `SetupFlow` that shows `BackTapOnboarding` if `hasSeenBackTapGuide` is false.
    - Cleanup: Remove the `showHowTo` variable and the `TextButton` ("How to use Back Tap for quick access?") section from `CurrentTasksView` (per GEST-02 and CONTEXT.md).
    - Ensure `markBackTapGuideAsSeen()` is called upon completion of the setup flow.
  </action>
  <verify>
    <automated>grep -v "How to use Back Tap for quick access?" app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</automated>
  </verify>
  <done>Onboarding integrated and legacy persistent guide removed.</done>
</task>

<task type="checkpoint:human-verify" gate="blocking">
  <what-built>One-Time Onboarding & UI Cleanup</what-built>
  <how-to-verify>
    1. Reset App data. Launch app.
    2. Verify Onboarding Guide appears during setup.
    3. Complete setup. Verify "Focus Mode" screen (CurrentTasksView) does NOT have the persistent "How-to" card at the bottom.
    4. Restart app. Verify onboarding does not reappear.
  </how-to-verify>
  <resume-signal>Onboarding is one-time and UI is clean</resume-signal>
</task>

</tasks>

<success_criteria>
- User sees onboarding once.
- Main UI is free of persistent instructions.
</success_criteria>

<output>
After completion, create `.planning/phases/04-frictionless-access-back-tap/04-03-SUMMARY.md`
</output>
