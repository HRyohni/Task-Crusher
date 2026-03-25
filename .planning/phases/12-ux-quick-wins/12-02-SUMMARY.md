---
phase: 12-ux-quick-wins
plan: "02"
subsystem: ui
tags: [compose, today-view, filter, delete, room, tasklist]

# Dependency graph
requires:
  - phase: 12-01
    provides: BrainDumpStep keyboard submit (contextual - same phase, independent feature)
provides:
  - deleteTask ViewModel method removing task from Room and refreshing widget
  - Today view filters completed tasks to show only those completed today
  - Incomplete tasks in Today view have a red delete icon button
affects: [12-03, 13-widget-overhaul]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - "Local extension function Task.completedToday() defined inside Composable for scoped date logic"
    - "Optional callback parameter (onDelete: (() -> Unit)? = null) to conditionally render UI actions"
    - "Callback threading: ZenStackApp -> MainShell -> CurrentTasksView -> TaskCard"

key-files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt
    - app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt

key-decisions:
  - "Use local extension function completedToday() inside CurrentTasksView composable to keep date logic close to its use without polluting global scope"
  - "onDelete parameter on TaskCard is nullable ((() -> Unit)?) so existing call-sites (ConfirmationStep future use) are unaffected by default"
  - "Delete button only shown when task.status != COMPLETED — users cannot delete completed tasks"

patterns-established:
  - "Pattern 1: Nullable callback pattern for optional UI actions on shared composables"
  - "Pattern 2: Local extension functions inside composables for scoped helper logic"

# Metrics
duration: 10min
completed: 2026-03-25
---

# Phase 12 Plan 02: UX Quick Wins — Today View Filter & Delete Summary

**Today view filters out prior-day completed tasks using completedAt timestamp comparison, and incomplete tasks gain a red delete button wired through to Room deletion**

## Performance

- **Duration:** 10 min
- **Started:** 2026-03-25T00:05:00Z
- **Completed:** 2026-03-25T00:15:00Z
- **Tasks:** 2
- **Files modified:** 2

## Accomplishments
- `deleteTask(task: Task)` added to TaskViewModel — removes from Room, refreshes widget
- Today tab hides completed tasks from prior sessions (completedAt not today)
- Incomplete tasks show a red Delete icon button; completed tasks do not
- `onDeleteTask` callback threaded from ZenStackApp through MainShell to CurrentTasksView to TaskCard
- Added `Instant` and `ZoneId` imports for epoch-millis to LocalDate conversion

## Task Commits

Each task was committed atomically:

1. **Task 1: Add deleteTask to TaskViewModel** - `e1a8f6a` (feat)
2. **Task 2: Filter today-only completed tasks and add delete button** - `312d03d` (feat)

**Plan metadata:** (docs commit follows)

## Files Created/Modified
- `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt` - Added `deleteTask` function
- `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt` - Today filter, delete callback chain, TaskCard delete icon

## Decisions Made
- Used a local extension function `Task.completedToday()` inside `CurrentTasksView` to keep timestamp conversion logic scoped and readable without polluting the global namespace.
- Made `onDelete` parameter on `TaskCard` nullable (`(() -> Unit)?`) so existing usages requiring no delete action compile without changes.
- Delete button guarded by `task.status != "COMPLETED"` — prevents deletion of completed tasks, protecting completion history.

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
None.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Phase 12 UX Quick Wins complete (both plans done).
- Widget overhaul (Phase 13) can proceed — `deleteTask` widget refresh is already handled.
- Calendar redesign (Phase 15) unaffected by these changes.

---
*Phase: 12-ux-quick-wins*
*Completed: 2026-03-25*
