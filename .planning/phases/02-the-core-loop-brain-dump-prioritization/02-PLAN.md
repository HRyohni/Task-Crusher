# Phase 02: The Core Loop — Plan

## Goal
Deliver the primary user workflow: capturing thoughts through a tactile "Brain Dump" and forcing a choice of 3 priorities ("Power 3").

## Must-Haves
- **Truths**:
  - User can rapidly dump thoughts into a persistent header input with tactile feedback.
  - User can select exactly 3 priorities, which visually "lift" (shadow) and "pop" (scale) on selection.
  - User is notified via double-vibration and a warning popup if they attempt to select a 4th priority.
  - Uncompleted priorities from previous sessions are suggested for the new session.
- **Artifacts**:
  - `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt` (BrainDumpStep & Power3Step updates)
  - `app/src/main/java/hr/fipu/organizationtool/data/TaskDao.kt` (New queries for suggestions)
  - `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt` (Selection constraint logic and persistence)
- **Key Links**:
  - `BrainDumpStep` calls `TaskViewModel.addTask`, triggering `HapticFeedbackType.TextHandleMove`.
  - `Power3Step` checks `TaskViewModel.selectedPriorityIds.size` to trigger warning feedback.
  - `TaskViewModel.saveSession` persists tasks instead of deleting them.

---

## Plan 02-01: Refined Brain Dump & Haptic Capture (Wave 1)
**Requirements**: DUMP-01
**Autonomous**: true
**Files Modified**: `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt`, `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`

<objective>
Implement the "Persistent Header" pattern for the Brain Dump and add tactile haptic feedback on task capture.
</objective>

<tasks>

<task type="auto">
  <name>Task 1: Persistent Header UI Update</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</files>
  <action>
    - Refactor `BrainDumpStep` to move the `OutlinedTextField` to the top as a persistent header.
    - Style the input field to be more prominent (using `ZenTheme` colors and `MaterialTheme.typography.headlineSmall`).
    - Reverse the task list order: newest tasks added must appear at the top of the list (directly below the header).
  </action>
  <verify>
    <automated>Run UI test confirming that adding a task puts it at the first index of the LazyColumn.</automated>
  </verify>
  <done>Brain Dump UI matches the "Persistent Header" decision with newest tasks at the top.</done>
</task>

<task type="auto">
  <name>Task 2: Implement Haptic Tick on Capture</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</files>
  <action>
    - Update the `IconButton` (Add) in `BrainDumpStep` to trigger `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.TextHandleMove)` upon successful task entry.
    - Ensure haptics only fire if the text is not blank.
  </action>
  <verify>
    <automated>Manually verify tactile "tick" on adding tasks in the emulator/device.</automated>
  </verify>
  <done>Haptic feedback provides tactile confirmation for every captured thought.</done>
</task>

</tasks>

---

## Plan 02-02: Tactical Selection & Constraints (Wave 2)
**Requirements**: PRIO-01, PRIO-02, PRIO-03
**Depends on**: 01
**Autonomous**: true
**Files Modified**: `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt`, `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`

<objective>
Enhance the prioritization screen with "ZenShadow" elevation, "Pop" motion, and a hard constraint of 3 tasks with sensory feedback.
</objective>

<tasks>

<task type="auto" tdd="true">
  <name>Task 1: Visual Polish — Shadow & Pop Motion</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</files>
  <behavior>
    - When a task is selected in `Power3Step`:
      - Apply `Modifier.zenShadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))` for elevation.
      - Apply a scale animation (Pop) using `animateFloatAsState` with `zenSpring` (ZenStiffness, ZenDamping).
  </behavior>
  <action>
    - Update `Power3Step` list items to use the defined visual feedback.
    - Use `Physics.kt` constants for spring behavior.
  </action>
  <verify>
    <automated>UI test checking that selected items have increased elevation and scale > 1.0 during transition.</automated>
  </verify>
  <done>Selected priorities visually "lift" and "pop" to confirm selection.</done>
</task>

<task type="auto">
  <name>Task 2: Constraint Logic — Warning & Double-Vibration</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt, app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt</files>
  <action>
    - Update `TaskViewModel.togglePriority` to detect when a user attempts to select a 4th priority.
    - If 4th selection attempt occurs:
      - Trigger a "double-vibration" haptic response (chain two `HapticFeedbackType.LongPress` calls with a 100ms delay or use `Vibrator` service).
      - Show a "Pill Warning Popup" (custom snackbar-style pill with `AnimatedVisibility`) saying "Select only 3 priorities."
  </action>
  <verify>
    <automated>Unit test confirming `selectedPriorityIds` never exceeds size 3.</automated>
  </verify>
  <done>Users are strictly limited to 3 priorities with clear sensory feedback on error.</done>
</task>

</tasks>

---

## Plan 02-03: Persistent Suggestions & Session State (Wave 3)
**Requirements**: CORE-02 (Refined)
**Depends on**: 02
**Autonomous**: true
**Files Modified**: `app/src/main/java/hr/fipu/organizationtool/data/TaskDao.kt`, `app/src/main/java/hr/fipu/organizationtool/data/TaskRepository.kt`, `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`, `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt`

<objective>
Enable task persistence across sessions and suggest uncompleted tasks from previous sessions during the "Power 3" setup.
</objective>

<tasks>

<task type="auto">
  <name>Task 1: Persistence & Suggestion Queries</name>
  <files>app/src/main/java/hr/fipu/organizationtool/data/TaskDao.kt, app/src/main/java/hr/fipu/organizationtool/data/TaskRepository.kt</files>
  <action>
    - Remove `deleteAllTasks()` from the `saveSession` flow in `TaskViewModel`.
    - Add `getSuggestedTasks()` to `TaskDao`:
      - Query for tasks where `status != 'COMPLETED'` and (were marked `isPriority` in last session OR are recent dump items).
    - Update `TaskRepository` to expose these suggestions as a Flow.
  </action>
  <verify>
    <automated>Integration test confirming that uncompleted tasks persist and are returned by the suggestion query after a session reset.</automated>
  </verify>
  <done>Data layer correctly manages task history and suggestion logic.</done>
</task>

<task type="auto">
  <name>Task 2: Suggestion UI Integration</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt, app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt</files>
  <action>
    - Update `SetupFlow` to display "Suggested for you" tasks at the top of the Brain Dump or as a pre-filled list.
    - Allow users to quickly "add" suggested tasks to the current pool or ignore them.
    - Ensure `saveSession` correctly marks the new selection as the current active priorities.
  </action>
  <verify>
    <automated>Manual verification: Complete a session with uncompleted tasks, start a new session, and confirm they appear as suggestions.</automated>
  </verify>
  <done>Starting a new session feels seamless with "carry-over" suggestions.</done>
</task>

</tasks>

---

## Success Criteria
1. **DUMP-01**: User can add tasks via a persistent header with a tactile "tick" confirmation.
2. **PRIO-01/02**: Selection is limited to 3 items, featuring "lift" (shadow) and "pop" (scale) effects.
3. **PRIO-03**: Attempting a 4th selection triggers double-vibration and a warning popup.
4. **CORE-02**: Uncompleted priorities from the previous session reappear as suggestions in the next.
