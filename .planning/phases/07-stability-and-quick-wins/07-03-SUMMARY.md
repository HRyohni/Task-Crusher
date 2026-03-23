---
phase: 07-stability-and-quick-wins
plan: "03"
subsystem: ui
tags: [onboarding, deep-link, accessibility, stateflow, compose, kotlin, android]

requires:
  - phase: 07-stability-and-quick-wins
    plan: "01"
    provides: TaskViewModel.saveSession state reset
  - phase: 04-onboarding-and-service
    provides: BackTapOnboarding composable, hasSeenBackTapGuide StateFlow, OnboardingRepository

provides:
  - hasSeenBackTapGuide initial value correctly false for fresh installs
  - BackTapOnboarding with one-tap deep-link to Android Accessibility Settings
  - Human-verified end-to-end onboarding flow on device

affects:
  - 08-navigation-shell
  - any future onboarding or accessibility guidance work

tech-stack:
  added: [android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS, android.content.Intent]
  patterns: [LocalContext for deep-link Intent in Composable, OutlinedButton for secondary actions]

key-files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt
    - app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt

key-decisions:
  - "stateIn initial value false is the correct semantic for 'has not yet seen' — true was a bug, not a design choice"
  - "Primary Button for settings deep-link, OutlinedButton for 'Got it' dismissal — hierarchy communicates the recommended action"

patterns-established:
  - "Pattern: Deep-link to system settings via Intent(Settings.ACTION_*) with FLAG_ACTIVITY_NEW_TASK"
  - "Pattern: Primary vs outlined button hierarchy for recommended action vs dismissal in onboarding flows"

duration: 12min
completed: 2026-03-23
---

# Phase 7 Plan 03: Onboarding Fix + Deep-Link Summary

**Back-tap onboarding guard fixed (false initial value) and one-tap deep-link added to Android Accessibility Settings from BackTapOnboarding screen**

## Performance

- **Duration:** 12 min
- **Started:** 2026-03-23T00:18:00Z
- **Completed:** 2026-03-23T00:30:00Z
- **Tasks:** 2 auto + 1 human-verify checkpoint
- **Files modified:** 2

## Accomplishments
- Fixed BUG-02: `hasSeenBackTapGuide` stateIn initial value corrected from `true` to `false` — fresh-install users now see the back-tap guide after their first session save
- Implemented UX-02: "Open Accessibility Settings" primary Button added to `BackTapOnboarding`, firing `Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)` — users no longer need to navigate manually through system settings
- "Got it" demoted to `OutlinedButton` — visual hierarchy makes the recommended action (open settings) obvious
- Human verification confirmed both flows work end-to-end on device

## Task Commits

Each task was committed atomically:

1. **Task 1: Fix hasSeenBackTapGuide initial value (BUG-02)** - `8f5d21f` (fix)
2. **Task 2: Add Accessibility Settings deep-link button (UX-02)** - `1571678` (feat)

**Plan metadata:** committed with Wave 1 summaries in `a6d36c6`

## Files Created/Modified
- `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt` - hasSeenBackTapGuide stateIn initial value: `true` -> `false`
- `app/src/main/java/hr/fipu/organizationtool/ui/components/BackTapOnboarding.kt` - Added Intent deep-link button, demoted Got it to OutlinedButton, added LocalContext import

## Decisions Made
- The `true` initial value was unambiguously a bug: the field's semantic is "has the user seen this guide", which is false until DataStore confirms otherwise. No design intent was served by `true`.
- `FLAG_ACTIVITY_NEW_TASK` is required when starting an Activity from a non-Activity context (Composable launched from a service/widget context); included for correctness.

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
None.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Phase 7 stability goals fully complete: 3 bugs fixed (BUG-01, BUG-02, BUG-03/BUG-04), 3 performance issues fixed (PERF-01, PERF-02, PERF-03), 1 UX improvement delivered (UX-02)
- Codebase is stable and ready for Phase 8 navigation shell work

---
*Phase: 07-stability-and-quick-wins*
*Completed: 2026-03-23*
