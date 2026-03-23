# ZenStack Roadmap

## Phases

- [ ] **Phase 1: Foundation & Data Architecture** - Establish the Kotlin 2.1/M3 foundation and Room Single Source of Truth.
- [ ] **Phase 2: The Core Loop (Brain Dump & Prioritization)** - Implement rapid task capture and the "Power 3" selection logic.
- [ ] **Phase 3: Glance Widget Integration** - Connect the Room DB to an interactive home screen widget with sync loop.
- [x] **Phase 4: Frictionless Access (Back Tap)** - Implement Accessibility Service for double-tap launch and onboarding. (completed 2026-03-23)
- [ ] **Phase 5: Tactile Interaction (Advanced UI)** - Add spring-based animations and interactive "bubbles" to the dump.
- [ ] **Phase 6: Sensory Feedback & Polish** - Implement low-latency audio rewards and final refinements.
- [ ] **Phase 7: Stability & Quick Wins** - Fix data integrity bugs, resolve performance regressions, and add the accessibility deep-link.
- [ ] **Phase 8: Navigation Shell & Confetti** - Introduce bottom navigation bar and complete-all confetti reward.
- [ ] **Phase 9: Scrollable Widget & Completion History** - Expand widget to show all tasks scrollably and record timestamps for historical queries.
- [ ] **Phase 10: Calendar Tab** - Surface completion history as a GitHub-style heatmap with day-detail drill-down.
- [ ] **Phase 11: Achievements Tab** - Implement streak, volume, and speed-run tracking with unlock notifications and full achievements display.

## Phase Details

### Phase 1: Foundation & Data Architecture
**Goal**: Establish a robust technical foundation and the data layer that will serve both App and Widget.
**Depends on**: Nothing
**Requirements**: CORE-01, CORE-02
**Success Criteria**:
  1. App compiles and runs with Kotlin 2.1 and Material 3 Expressive theme.
  2. Room database successfully persists and retrieves a test task.
**Plans**:
- [ ] 01-01-PLAN.md — Core Infrastructure & Tooling Upgrade (Kotlin 2.1, KSP, Koin 4.0)
- [ ] 01-02-PLAN.md — Zen Design System Implementation (M3 Expressive, Palette, Physics)
- [ ] 01-03-PLAN.md — Room KMP Data Layer (Multi-Instance Invalidation, Repository)
- [ ] 01-04-PLAN.md — Foundation Verification (Integration Lab Screen)

### Phase 2: The Core Loop (Brain Dump & Prioritization)
**Goal**: Deliver the primary user workflow: capturing thoughts and forcing a choice of 3 priorities.
**Depends on**: Phase 1
**Requirements**: DUMP-01, PRIO-01, PRIO-02, PRIO-03
**Success Criteria**:
  1. User can type a task in a minimalist "Void Input" and see it saved to the list.
  2. User can select exactly 3 tasks from the pool for their session.
  3. User is prevented from selecting a 4th task, with non-selected items dimming dynamically.
**Plans**: 3 plans
- [x] 02-01-PLAN.md — Refined Brain Dump & Haptic Capture (DUMP-01)
- [ ] 02-02-PLAN.md — Tactical Selection & Constraints (PRIO-01, PRIO-02, PRIO-03)
- [ ] 02-03-PLAN.md — Persistent Suggestions & Session State (CORE-02 refinement)

### Phase 3: Glance Widget Integration
**Goal**: Bridge the App and Home Screen via a synchronized Glance widget.
**Depends on**: Phase 2
**Requirements**: WIDG-01, WIDG-02
**Success Criteria**:
  1. User can add the ZenStack widget to their home screen and see their current Top 3 tasks.
  2. User can check off a task directly on the widget, updating the Room DB state.
  3. The widget's progress tracker (e.g., "1/3 Done") updates immediately upon task completion.
**Plans**: TBD
**UI hint**: yes

### Phase 4: Frictionless Access (Back Tap)
**Goal**: Minimize the gap between thought and action with gesture-based launching.
**Depends on**: Phase 1
**Requirements**: GEST-01, GEST-02
**Success Criteria**:
  1. User can launch the ZenStack app by double-tapping the back of their device.
  2. User can complete a visual onboarding guide that explains how to enable system-level gestures.
**Plans**: 3 plans
- [ ] 04-01-PLAN.md — Accessibility Service Foundation (GEST-01 Infrastructure)
- [ ] 04-02-PLAN.md — Gesture Detection Logic (Accelerometer processing)
- [ ] 04-03-PLAN.md — One-Time Onboarding Experience (GEST-02 UI & Persistence)
**UI hint**: yes

### Phase 5: Tactile Interaction (Advanced UI)
**Goal**: Enhance the "Brain Dump" with the intended tactile, physics-based feel.
**Depends on**: Phase 2
**Requirements**: DUMP-02, DUMP-03
**Success Criteria**:
  1. Captured tasks appear as floating bubbles with spring-based motion.
  2. User can tap an 'X' on a bubble to delete it with a visual "pop".
  3. User can tap a bubble to edit the existing task text.
**Plans**: TBD
**UI hint**: yes

### Phase 6: Sensory Feedback & Polish
**Goal**: Add the final layer of satisfaction and handle system-level edge cases.
**Depends on**: Phase 3
**Requirements**: WIDG-03
**Success Criteria**:
  1. A low-latency "success" sound plays when a task is checked off via the widget.
  2. Audio feedback respects the device's "Do Not Disturb" and silent mode settings.
**Plans**: TBD

### Phase 7: Stability & Quick Wins
**Goal**: Users experience a reliable, data-safe app with no silent data loss, reduced battery impact, and a direct path to enabling back-tap.
**Depends on**: Phase 4 (v1.0 completion)
**Requirements**: BUG-01, BUG-02, BUG-03, BUG-04, PERF-01, PERF-02, PERF-03, UX-02
**Success Criteria**:
  1. Updating the app does not wipe existing tasks — Room migrations run without destructive fallback.
  2. A fresh install shows the back-tap onboarding guide on first launch (not silently skipped).
  3. Running a session multiple times does not duplicate previously-suggested tasks in the database.
  4. Battery usage by the accelerometer sensor is measurably reduced (SENSOR_DELAY_NORMAL).
  5. User can tap a button on the back-tap setup screen and land directly on the Android Accessibility Settings page.
**Plans**: 3 plans
- [ ] 07-01-PLAN.md — Data integrity fixes (Room migration, exportSchema, saveSession reset)
- [ ] 07-02-PLAN.md — Performance fixes (SENSOR_DELAY_NORMAL, widget repository pattern)
- [ ] 07-03-PLAN.md — Onboarding fix + accessibility deep-link button

### Phase 8: Navigation Shell & Confetti
**Goal**: Users can navigate between app sections via a persistent bottom bar, and completing all 3 priorities delivers a satisfying confetti reward.
**Depends on**: Phase 7
**Requirements**: NAV-01, NAV-02, UX-01
**Success Criteria**:
  1. A bottom navigation bar with Today, Calendar, and Achievements tabs is visible throughout the app.
  2. Tapping "Today" shows the same session/task UI that existed before bottom nav was added — nothing regresses.
  3. Completing all 3 priority tasks triggers a full-screen confetti animation.
**Plans**: 2 plans
- [ ] 08-01-PLAN.md — Bottom nav shell + konfetti dependency (NAV-01, NAV-02)
- [ ] 08-02-PLAN.md — Confetti celebration overlay on all-complete (UX-01)

### Phase 9: Scrollable Widget & Completion History
**Goal**: The home screen widget shows the full task picture, and the app starts recording when tasks are completed so history can be queried by date.
**Depends on**: Phase 7
**Requirements**: WIDG-04, WIDG-05, CAL-03
**Success Criteria**:
  1. The widget is scrollable and shows all priority tasks without a hard cap.
  2. Brain dump (non-priority) tasks appear in a scrollable section below the priorities on the widget.
  3. Completing a task records a timestamp that can be queried to retrieve all tasks finished on a given day.
**Plans**: 2 plans
- [ ] 09-01-PLAN.md — Completion timestamp data layer: Task.completedAt, DB v4 migration, DAO calendar query, Repository + widget callback update (CAL-03)
- [ ] 09-02-PLAN.md — Scrollable widget with LazyColumn showing all priority and brain dump tasks (WIDG-04, WIDG-05)

### Phase 10: Calendar Tab
**Goal**: Users can review their completion history at a glance and drill into any past day.
**Depends on**: Phase 9
**Requirements**: CAL-01, CAL-02
**Success Criteria**:
  1. The Calendar tab displays a GitHub-style heatmap where each day is colored by number of tasks completed.
  2. User can tap any day on the heatmap to see a list of the specific tasks completed that day.
  3. Days with zero completions are visually distinct from days with activity.
**Plans**: TBD

### Phase 11: Achievements Tab
**Goal**: Users are rewarded for consistent use and ambitious sessions through visible progress, unlock moments, and a persistent gallery of achievements.
**Depends on**: Phase 9
**Requirements**: ACH-01, ACH-02, ACH-03, ACH-04, ACH-05
**Success Criteria**:
  1. The app tracks and displays the user's current daily streak (consecutive days with at least one completed session).
  2. Volume milestones (10, 50, 100 tasks completed) are tracked and surface as achievements.
  3. Completing all 3 priorities within 1 hour of session start registers a "Speed Run" achievement.
  4. Unlocking a new achievement shows a pop-up notification at the moment it is earned.
  5. The Achievements tab lists every achievement (locked and unlocked) with a progress indicator for each.
**Plans**: TBD

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Data Architecture | 0/4 | Not started | - |
| 2. The Core Loop | 0/3 | Not started | - |
| 3. Glance Widget Integration | 0/1 | Not started | - |
| 4. Frictionless Access (Back Tap) | 1/1 | Complete   | 2026-03-23 |
| 5. Tactile Interaction | 0/1 | Not started | - |
| 6. Sensory Feedback & Polish | 0/1 | Not started | - |
| 7. Stability & Quick Wins | 0/3 | Not started | - |
| 8. Navigation Shell & Confetti | 0/2 | Not started | - |
| 9. Scrollable Widget & Completion History | 0/2 | Not started | - |
| 10. Calendar Tab | 0/? | Not started | - |
| 11. Achievements Tab | 0/? | Not started | - |
