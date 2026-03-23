# Phase 04 Validation: Frictionless Access (Back Tap)

## Goal
Verify the implementation of the Accessibility Service for double-tap launch and the one-time onboarding experience.

## Success Criteria
- [ ] **GEST-01**: Accessibility Service detected.
- [ ] **GEST-01**: Double-tap on the back of the device launches the app.
- [ ] **GEST-01**: The app launches directly into the `CurrentTasksView`.
- [ ] **GEST-02**: A one-time onboarding guide is shown during initial setup.
- [ ] **GEST-02**: The guide is correctly dismissed and not shown again.
- [ ] **Minimalism**: No persistent "How-To" card remains in the `CurrentTasksView`.

## Verification Strategy

### Automated Tests
- **Wave 0: Integration Tests**
  - `app/src/androidTest/java/hr/fipu/organizationtool/BackTapTest.kt`: Test that the Accessibility Service correctly detects gestures and triggers the intent.
  - `app/src/androidTest/java/hr/fipu/organizationtool/OnboardingTest.kt`: Test that the onboarding guide is shown once and respects the DataStore state.

### Manual Verification
1. **Accessibility Service Setup**:
   - Install the app.
   - Go to System Settings -> Accessibility -> Downloaded Services -> ZenStack.
   - Enable the service.
2. **Gesture Verification**:
   - Background the app or turn off the screen (if screen-on gating is disabled for testing).
   - Double-tap the back of the device.
   - Verify the app launches to `CurrentTasksView`.
3. **Onboarding Verification**:
   - Clear app data.
   - Launch the app.
   - Follow the `SetupFlow` and verify the one-time onboarding guide appears.
   - Complete setup and verify the guide is gone.
   - Restart the app and verify the guide does not reappear.
   - Verify no persistent "How-to" card is visible in `CurrentTasksView`.

## Traceability
| Requirement | Test Case | Status |
|-------------|-----------|--------|
| GEST-01     | BackTapTest.kt | Pending |
| GEST-02     | OnboardingTest.kt | Pending |
