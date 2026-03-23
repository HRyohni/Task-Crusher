---
phase: 09-scrollable-widget-and-completion-history
plan: 02
subsystem: ui
tags: [glance, widget, lazy-column, scrollable, android-widget]

requires:
  - phase: 09-01
    provides: TaskRepository.allNonPriorityTasks (unbounded Flow)
  - phase: 07-stability-and-quick-wins
    provides: TaskRepository pattern, ZenStackWidget base implementation

provides:
  - ZenStackWidget refactored to use Glance LazyColumn — scrollable task list
  - allNonPriorityTasks unbounded property on TaskRepository
  - All priority tasks visible in widget (no hard cap)
  - All brain dump tasks visible below priority section (no hard cap)

affects:
  - 10-calendar (calendar tab; widget scrollability complete)

tech-stack:
  added:
    - androidx.glance.appwidget.lazy.LazyColumn (new Glance API usage)
    - androidx.glance.appwidget.lazy.items (Glance LazyColumn items extension)
  patterns:
    - "Glance LazyColumn with item{} and items{} blocks replaces forEach in Column for scrollable widget lists"
    - "cornerRadius cannot be applied to Glance LazyColumn — widget host clips corners via XML configuration"
    - "Widget-wide clickable removed when using LazyColumn — individual TaskItem rows retain actionRunCallback clicks"

key-files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/data/TaskRepository.kt
    - app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt

key-decisions:
  - "allNonPriorityTasks uses getNonPriorityTasks(Int.MAX_VALUE) — reuses existing DAO query without adding a new method"
  - "Removed widget-wide clickIntent/actionStartActivity — conflicts with LazyColumn scrolling; individual row clicks preserved"
  - "cornerRadius removed from LazyColumn (Glance API limitation) — widget XML handles corner clipping"

patterns-established:
  - "Glance LazyColumn structure: item{} for static rows (header, section labels, progress bar), items(){} for dynamic task lists"

duration: 7min
completed: 2026-03-23
---

# Phase 09 Plan 02: Scrollable Glance Widget Summary

**ZenStackWidget refactored from static Column with forEach to Glance LazyColumn with items(), showing all priority and brain dump tasks without a hard cap**

## Performance

- **Duration:** 7 min
- **Started:** 2026-03-23T22:42:00Z
- **Completed:** 2026-03-23T22:49:00Z
- **Tasks:** 2
- **Files modified:** 2

## Accomplishments
- Added `allNonPriorityTasks: Flow<List<Task>>` backed by `getNonPriorityTasks(Int.MAX_VALUE)` to TaskRepository
- Replaced widget's `Column` + `forEach` layout with a `LazyColumn` using `item{}` and `items(){}` blocks
- All priority tasks now rendered via `items(priorityTasks)` — no hard cap
- All brain dump tasks rendered via `items(otherTasks)` below section header — no hard cap (was previously capped at 3)
- Removed widget-wide `clickable(actionStartActivity)` which conflicts with LazyColumn scrolling
- Individual TaskItem row click behavior preserved via `actionRunCallback<ToggleTaskCallback>`

## Task Commits

Each task was committed atomically:

1. **Task 1: Add allNonPriorityTasks property to TaskRepository** - `e02181c` (feat)
2. **Task 2: Refactor ZenStackWidget to use LazyColumn for all tasks** - `943a510` (feat)

**Plan metadata:** (docs commit follows)

## Files Created/Modified
- `app/src/main/java/hr/fipu/organizationtool/data/TaskRepository.kt` - Added `allNonPriorityTasks` unbounded property after `priorityTasks`
- `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt` - Full layout refactor to LazyColumn; removed Column, forEach, clickIntent, cornerRadius; added LazyColumn/items imports

## Decisions Made
- `allNonPriorityTasks` uses `getNonPriorityTasks(Int.MAX_VALUE)` — avoids adding a new DAO method; existing LIMIT query with Int.MAX_VALUE is functionally unbounded
- Removed widget-wide click (actionStartActivity on the outer Column) — it conflicts with LazyColumn scroll gestures; individual TaskItem rows already handle taps correctly
- `cornerRadius` removed — Glance API does not support it on LazyColumn; widget host XML configuration clips corners instead

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness
- Widget is now scrollable and shows all tasks without a hard cap — WIDG-04 and WIDG-05 satisfied
- Phase 09 complete; Phase 10 (Calendar tab) can proceed — completedAt timestamps are being recorded

## Self-Check: PASSED

All files verified present. All commits verified in git log.

---
*Phase: 09-scrollable-widget-and-completion-history*
*Completed: 2026-03-23*
