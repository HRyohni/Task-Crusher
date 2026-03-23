---
phase: 10-calendar-tab
plan: "01"
subsystem: ui
tags: [viewmodel, stateflow, calendar, heatmap, coroutines, room, localdate]

# Dependency graph
requires:
  - phase: 09-widget-history
    provides: "TaskRepository.getTasksCompletedOn(start, end): Flow<List<Task>> and completedAt timestamp on Task"
provides:
  - "TaskViewModel.completionHistory: StateFlow<Map<LocalDate, Int>> — 90-day date-to-count map"
  - "TaskViewModel.selectedDay: StateFlow<LocalDate?> — currently selected calendar day"
  - "TaskViewModel.tasksForSelectedDay: StateFlow<List<Task>> — reactive task list for selected day"
  - "TaskViewModel.selectDay(date: LocalDate) — mutates _selectedDay"
  - "TaskViewModel.loadCompletionHistory() — snapshot scan of 90 days via .first()"
affects: [10-02-calendar-ui, CalendarScreen]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "flatMapLatest on StateFlow for reactive day-detail updates"
    - ".first() for one-shot Room Flow snapshot (avoids indefinite suspension from .collect{})"
    - "init block calls loadCompletionHistory() to populate heatmap data on ViewModel creation"

key-files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt

key-decisions:
  - "Use .first() instead of .collect { return@collect } for snapshot reads — Room Flows never terminate, .collect{} would suspend indefinitely on the first day's query"
  - "completionHistory is populated once at ViewModel init; tasksForSelectedDay uses flatMapLatest for reactive updates"
  - "flatMapLatest triggers ExperimentalCoroutinesApi warning (not error) — accepted per plan design, no opt-in added"

patterns-established:
  - "Snapshot pattern: repository.getFlow(args).first() for point-in-time reads in coroutine loops"
  - "Calendar state trio: completionHistory (history map) + selectedDay (selection) + tasksForSelectedDay (reactive detail)"

# Metrics
duration: 1min
completed: 2026-03-23
---

# Phase 10 Plan 01: ViewModel Calendar State Summary

**Calendar state added to TaskViewModel: 90-day heatmap map via .first() snapshot loop, reactive day-detail list via flatMapLatest, and selectDay() mutator**

## Performance

- **Duration:** 1 min
- **Started:** 2026-03-23T22:46:31Z
- **Completed:** 2026-03-23T22:47:15Z
- **Tasks:** 1
- **Files modified:** 1

## Accomplishments
- Added `completionHistory: StateFlow<Map<LocalDate, Int>>` populated at ViewModel init across 90 days
- Added `tasksForSelectedDay: StateFlow<List<Task>>` that reacts to `_selectedDay` via `flatMapLatest`
- Added `selectDay(date: LocalDate)` mutator for `_selectedDay`
- Applied critical correction: `.first()` instead of `.collect { return@collect }` to prevent indefinite suspension

## Task Commits

Each task was committed atomically:

1. **Task 1: Add calendar StateFlows and selectDay method to TaskViewModel** - `918f6b6` (feat)

## Files Created/Modified
- `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt` - Added completionHistory, selectedDay, tasksForSelectedDay StateFlows, selectDay(), loadCompletionHistory(), and init block

## Decisions Made
- Used `.first()` for snapshot reads in `loadCompletionHistory()` — Room Flows emit continuously and `.collect { return@collect }` would suspend indefinitely on the first emission per day
- `tasksForSelectedDay` uses `flatMapLatest` for reactive updates when the user taps a different day
- ExperimentalCoroutinesApi warning from `flatMapLatest` accepted as-is (warning only, not error)

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] Replaced .collect { return@collect } with .first() in loadCompletionHistory()**
- **Found during:** Task 1 (pre-implementation review of plan instructions)
- **Issue:** Plan described using `.collect { tasks -> ... return@collect }` as a "snapshot" but Room Flows never terminate — `.collect{}` suspends indefinitely after the first emission, blocking the 90-day loop from advancing to day 2
- **Fix:** Used `repository.getTasksCompletedOn(startOfDay, endOfDay).first()` which returns a single snapshot list without hanging
- **Files modified:** app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt
- **Verification:** Build passes, logic correct — .first() terminates after one emission
- **Committed in:** 918f6b6 (Task 1 commit)

---

**Total deviations:** 1 auto-fixed (1 bug — infinite suspension prevention)
**Impact on plan:** Critical correctness fix. Without .first(), loadCompletionHistory() would hang after processing day 0, leaving the historyMap with only one entry and never emitting the full 90-day result.

## Issues Encountered
None

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Plan 10-02 (CalendarScreen UI + ZenStackApp wiring) can proceed immediately
- completionHistory, selectedDay, tasksForSelectedDay, and selectDay() are all available in TaskViewModel

---
*Phase: 10-calendar-tab*
*Completed: 2026-03-23*
