# Phase 02: The Core Loop - Context

**Gathered:** March 23, 2026
**Status:** Ready for planning

<domain>
## Phase Boundary

Implementing the primary user workflow: the "Brain Dump" and the "Power 3" prioritization. This phase builds on the Phase 1 foundation (Room, ZenTheme, shadows) to deliver the functional core of ZenStack.

</domain>

<decisions>
## Implementation Decisions

### DUMP-01: Void Input (Brain Dump)
- **Persistent Header:** Use a prominent text input at the top of the list. New tasks are added to the pool and push existing items down for efficient batch entry.
- **Haptic (Capture):** Apply a light "tick" (`HapticFeedbackType.TextHandleMove`) upon successful task entry to provide immediate tactile confirmation.

### PRIO-01: The Power 3 (Prioritization)
- **Tactile Elevation:** Selected priorities MUST use `Modifier.zenShadow()` to visually "lift" off the surface, creating a distinct physical hierarchy.
- **Pop Motion:** Selected items should use a spring-based scale animation ("Pop") to confirm selection.
- **Constraint (PRIO-02):** If a user attempts to select a 4th priority:
  - Trigger a **Double-vibration** haptic response.
  - Display a popup/Toast message: "You can only select 3 priorities."

### Session Lifecycle
- **Persistence & Suggestions:** Do NOT delete old tasks in `saveSession`. 
  - Brain dump items from the previous session should be suggested/available when starting a new session.
  - Uncompleted priority tasks from the previous session MUST be automatically carried forward/suggested for the new "Power 3" pool.

</decisions>

<canonical_refs>
## Canonical References

### Project Core
- `.planning/PROJECT.md` — Vision and Core Value.
- `.planning/REQUIREMENTS.md` — DUMP-01, PRIO-01, PRIO-02, PRIO-03.
- `.planning/ROADMAP.md` — Phase 2 Goal and Success Criteria.

### Foundation Assets
- `.planning/phases/01-foundation-data-architecture/01-CONTEXT.md` — Zen Palette, Shadows, and Physics constants.
- `app/src/main/java/hr/fipu/organizationtool/ui/theme/Theme.kt` — Use `ZenTheme`.
- `app/src/main/java/hr/fipu/organizationtool/ui/theme/Shadows.kt` — Use `Modifier.zenShadow()`.
- `app/src/main/java/hr/fipu/organizationtool/ui/theme/Physics.kt` — Use `ZenStiffness`/`ZenDamping` for the "Pop" motion.

</canonical_refs>

<code_context>
## Codebase Insights

### Reusable Patterns
- `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt` — Contains the current `SetupFlow` and `BrainDumpStep` which need to be upgraded to match the "Persistent Header" and "Tactile Selection" decisions.
- `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt` — Needs to be updated to handle task persistence (not deleting) and suggestions for uncompleted items.

### Integration Points
- `TaskRepository.kt` — Will need queries to retrieve "suggested" tasks (uncompleted priorities and recent dump items).

</code_context>

<deferred>
## Deferred Ideas
- **Physics-based Bubbles:** Floating, draggable bubbles are still deferred to Phase 5. Phase 2 uses a list-based dump with tactile headers and shadows.
- **Archive History View:** A dedicated history/archive screen is out of scope for the MVP.

</deferred>

---

*Phase: 02-the-core-loop-brain-dump-prioritization*
*Context gathered: March 23, 2026*
