---
phase: 12-ux-quick-wins
plan: "01"
subsystem: ui
tags: [compose, keyboard, imeaction, textfield, braindump]

# Dependency graph
requires: []
provides:
  - BrainDumpStep OutlinedTextField with ImeAction.Done keyboard submit
  - Shared submitTask lambda deduplicating Add button and Enter key logic
affects: [12-02]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "Extract submit logic to local lambda to share between UI triggers (button click, keyboard action)"
    - "KeyboardOptions(imeAction) + KeyboardActions(onDone) for IME submission in OutlinedTextField"

key-files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt

key-decisions:
  - "Extract submitTask lambda before Column so both trailingIcon and onDone share identical behavior without duplication"

patterns-established:
  - "Pattern 1: Local submitTask lambda pattern for shared keyboard/button submit in Compose TextFields"

# Metrics
duration: 5min
completed: 2026-03-25
---

# Phase 12 Plan 01: UX Quick Wins — Keyboard Submit Summary

**BrainDumpStep OutlinedTextField now submits on Enter/Done via ImeAction.Done and shared submitTask lambda**

## Performance

- **Duration:** 5 min
- **Started:** 2026-03-25T00:00:00Z
- **Completed:** 2026-03-25T00:05:00Z
- **Tasks:** 1
- **Files modified:** 1

## Accomplishments
- BrainDumpStep TextField responds to Enter/Done key the same as tapping Add
- Blank input on Enter is a no-op (guarded by `isNotBlank`)
- Eliminated code duplication between trailingIcon and keyboard submit via shared `submitTask` lambda
- Added `KeyboardOptions`, `KeyboardActions`, `ImeAction` imports

## Task Commits

Each task was committed atomically:

1. **Task 1: Add keyboard submit to BrainDumpStep TextField** - `076ac6c` (feat)

**Plan metadata:** (docs commit follows)

## Files Created/Modified
- `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt` - Added submitTask lambda, KeyboardOptions/Actions to OutlinedTextField, simplified trailingIcon onClick

## Decisions Made
- Extracted `submitTask` lambda before the `Column` composable so both the `trailingIcon` `IconButton` and `KeyboardActions(onDone)` call the same function, preventing behavioral drift.

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
None.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Plan 12-01 complete. Plan 12-02 can proceed.
- Today-view filter and delete capability are independent of this keyboard change.

---
*Phase: 12-ux-quick-wins*
*Completed: 2026-03-25*
