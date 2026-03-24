---
phase: quick-1
plan: 01
subsystem: service
tags: [accelerometer, sensor, gesture, back-tap, signal-processing]

# Dependency graph
requires: []
provides:
  - TapTap heuristic back-tap detection pipeline in ZenStackAccessibilityService
  - slope -> low-pass -> high-pass -> peak ratio -> timing deque -> gesture throttle signal chain
affects: [back-tap-feature, accessibility-service]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "Signal processing pipeline: slope first-difference -> low-pass filter -> high-pass filter -> adaptive peak tracking"
    - "Double-tap validation via ArrayDeque timestamp window with min/max gap enforcement and gesture throttle"

key-files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt

key-decisions:
  - "SENSOR_DELAY_GAME used instead of SENSOR_DELAY_NORMAL to capture tap impulse with sufficient fidelity"
  - "Peak ratio guard (0 < ratio < PEAK_RATIO_MAX=3.0) filters asymmetric jolts that are not real taps"
  - "GESTURE_THROTTLE_MS=500 prevents double-trigger on a single double-tap gesture"

patterns-established:
  - "processSample(z: Float) separates sensor dispatch from signal logic for testability"

# Metrics
duration: 5min
completed: 2026-03-24
---

# Quick Task 1: Replace Naive Z Threshold Back-Tap Detection Summary

**Replaced single-threshold Z jolts detector with a full TapTap signal-processing pipeline (slope, low-pass, high-pass, adaptive peak ratio, timing deque, gesture throttle) running at SENSOR_DELAY_GAME**

## Performance

- **Duration:** ~5 min
- **Started:** 2026-03-24T00:00:00Z
- **Completed:** 2026-03-24T00:05:00Z
- **Tasks:** 1
- **Files modified:** 1

## Accomplishments
- Removed naive `tapThreshold = 15.0f` / `lastTapTime` / `tapWindowMin` / `tapWindowMax` fields entirely
- Implemented 9-stage signal chain: slope -> low-pass (LOWPASS_ALPHA=0.2) -> high-pass -> peak tracking with PEAK_WINDOW=64 decay -> adaptive noise gate -> peak ratio check -> stale-timestamp purge -> min-gap enforcement -> double-tap gesture with throttle
- Sensor registration upgraded from SENSOR_DELAY_NORMAL to SENSOR_DELAY_GAME

## Task Commits

1. **Task 1: Implement TapTap heuristic pipeline** - `c662419` (feat)

## Files Created/Modified
- `app/src/main/java/hr/fipu/organizationtool/service/ZenStackAccessibilityService.kt` - Full rewrite of sensor-detection logic; lifecycle, DisplayListener, and launchMainActivity unchanged

## Decisions Made
- SENSOR_DELAY_GAME chosen for sufficient sample rate to capture the short impulse of a finger tap on the phone back
- Peak ratio max of 3.0 follows TapTap/Columbus heuristic: real taps have balanced positive and negative lobes; large ratios indicate non-tap jolts
- GESTURE_THROTTLE_MS prevents re-triggering if the deque still holds timestamps from the prior gesture

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None. Build succeeded with 0 errors on first compile (4 pre-existing deprecation warnings, all unrelated to this change).

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- Back-tap detection is production-ready for on-device testing
- If false-positive rate on real hardware is still high, PEAK_RATIO_MAX or TAP_MAX_GAP_MS are the primary tuning knobs

---
*Phase: quick-1*
*Completed: 2026-03-24*
