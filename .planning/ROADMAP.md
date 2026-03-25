# ZenStack Roadmap

## Milestones

- ✅ **v1.0 MVP** - Phases 1-4 (shipped 2026-03-23)
- ✅ **v1.1 Engagement** - Phases 7-11 (shipped 2026-03-24)
- 🚧 **v1.2 Polish & Power** - Phases 12-17 (in progress)

## Phases

<details>
<summary>✅ v1.0 MVP (Phases 1-4) - SHIPPED 2026-03-23</summary>

- [x] **Phase 1: Foundation & Data Architecture** - Establish the Kotlin 2.1/M3 foundation and Room Single Source of Truth.
- [x] **Phase 2: The Core Loop (Brain Dump & Prioritization)** - Implement rapid task capture and the "Power 3" selection logic.
- [x] **Phase 3: Glance Widget Integration** - Connect the Room DB to an interactive home screen widget with sync loop.
- [x] **Phase 4: Frictionless Access (Back Tap)** - Implement Accessibility Service for double-tap launch and onboarding.

### Phase 1: Foundation & Data Architecture
**Goal**: Establish a robust technical foundation and the data layer that will serve both App and Widget.
**Depends on**: Nothing
**Requirements**: CORE-01, CORE-02
**Success Criteria** (what must be TRUE):
  1. App compiles and runs with Kotlin 2.1 and Material 3 Expressive theme.
  2. Room database successfully persists and retrieves a test task.
**Plans**: 4 plans
- [x] 01-01-PLAN.md — Core Infrastructure & Tooling Upgrade (Kotlin 2.1, KSP, Koin 4.0)
- [x] 01-02-PLAN.md — Zen Design System Implementation (M3 Expressive, Palette, Physics)
- [x] 01-03-PLAN.md — Room KMP Data Layer (Multi-Instance Invalidation, Repository)
- [x] 01-04-PLAN.md — Foundation Verification (Integration Lab Screen)

### Phase 2: The Core Loop (Brain Dump & Prioritization)
**Goal**: Deliver the primary user workflow: capturing thoughts and forcing a choice of 3 priorities.
**Depends on**: Phase 1
**Requirements**: DUMP-01, PRIO-01, PRIO-02, PRIO-03
**Success Criteria** (what must be TRUE):
  1. User can type a task in a minimalist "Void Input" and see it saved to the list.
  2. User can select exactly 3 tasks from the pool for their session.
  3. User is prevented from selecting a 4th task, with non-selected items dimming dynamically.
**Plans**: 3 plans
- [x] 02-01-PLAN.md — Refined Brain Dump & Haptic Capture (DUMP-01)
- [x] 02-02-PLAN.md — Tactical Selection & Constraints (PRIO-01, PRIO-02, PRIO-03)
- [x] 02-03-PLAN.md — Persistent Suggestions & Session State (CORE-02 refinement)

### Phase 3: Glance Widget Integration
**Goal**: Bridge the App and Home Screen via a synchronized Glance widget.
**Depends on**: Phase 2
**Requirements**: WIDG-01, WIDG-02
**Success Criteria** (what must be TRUE):
  1. User can add the ZenStack widget to their home screen and see their current Top 3 tasks.
  2. User can check off a task directly on the widget, updating the Room DB state.
  3. The widget's progress tracker updates immediately upon task completion.
**Plans**: 3 plans
- [x] 03-01-PLAN.md — Glance Widget Foundation
- [x] 03-02-PLAN.md — Widget Action Loop & Room Sync
- [x] 03-03-PLAN.md — Widget Polish & Edge Cases

### Phase 4: Frictionless Access (Back Tap)
**Goal**: Minimize the gap between thought and action with gesture-based launching.
**Depends on**: Phase 1
**Requirements**: GEST-01, GEST-02
**Success Criteria** (what must be TRUE):
  1. User can launch the ZenStack app by double-tapping the back of their device.
  2. User can complete a visual onboarding guide that explains how to enable system-level gestures.
**Plans**: 3 plans
- [x] 04-01-PLAN.md — Accessibility Service Foundation (GEST-01 Infrastructure)
- [x] 04-02-PLAN.md — Gesture Detection Logic (Accelerometer processing)
- [x] 04-03-PLAN.md — One-Time Onboarding Experience (GEST-02 UI & Persistence)

</details>

<details>
<summary>✅ v1.1 Engagement (Phases 7-11) - SHIPPED 2026-03-24</summary>

- [x] **Phase 7: Stability & Data Integrity** - Room safe migrations, widget performance, critical bug fixes.
- [x] **Phase 8: Navigation Shell & Confetti** - Bottom nav (Today/Calendar/Achievements), confetti celebration.
- [x] **Phase 9: Scrollable Widget & Completion History** - All tasks visible in widget, completedAt timestamp.
- [x] **Phase 10: Calendar Heatmap** - 90-day GitHub-style heatmap with tappable day drill-down.
- [x] **Phase 11: Achievements System** - 8 achievements with DB tracking and in-app unlock pop-up.

### Phase 7: Stability & Data Integrity
**Goal**: Harden the data layer so existing users can upgrade without data loss and the widget performs efficiently.
**Depends on**: Phase 4
**Requirements**: BUG-01, BUG-02, BUG-03, BUG-04, PERF-01, PERF-02, PERF-03, UX-02
**Success Criteria** (what must be TRUE):
  1. App upgrades from v2 schema without crashing or losing tasks.
  2. Widget reads data via Repository pattern, never AppDatabase directly.
  3. Accelerometer runs at SENSOR_DELAY_NORMAL (5Hz).
**Plans**: 3 plans
- [x] 07-01-PLAN.md — Room Migrations & Schema Safety
- [x] 07-02-PLAN.md — Widget Performance & Repository Pattern
- [x] 07-03-PLAN.md — Bug Fixes & Deep-Link UX

### Phase 8: Navigation Shell & Confetti
**Goal**: Users can navigate between Today, Calendar, and Achievements tabs; all-3-complete triggers confetti.
**Depends on**: Phase 7
**Requirements**: NAV-01, NAV-02, UX-01
**Success Criteria** (what must be TRUE):
  1. User can tap between Today, Calendar, and Achievements bottom tabs.
  2. Existing session UI is fully preserved in the Today tab.
  3. Confetti fires when all 3 priorities are marked complete.
**Plans**: 2 plans
- [x] 08-01-PLAN.md — Bottom Navigation Shell
- [x] 08-02-PLAN.md — Confetti Celebration

### Phase 9: Scrollable Widget & Completion History
**Goal**: Widget shows all tasks; completion timestamps are recorded for calendar history.
**Depends on**: Phase 8
**Requirements**: WIDG-04, WIDG-05, CAL-03
**Success Criteria** (what must be TRUE):
  1. Widget scrolls to show all priority and brain dump tasks (no hard cap).
  2. Completing a task records a timestamp in the Room DB.
**Plans**: 2 plans
- [x] 09-01-PLAN.md — Scrollable Widget (LazyColumn)
- [x] 09-02-PLAN.md — Completion Timestamps (completedAt)

### Phase 10: Calendar Heatmap
**Goal**: Users can see their historical task completion activity in a visual calendar.
**Depends on**: Phase 9
**Requirements**: CAL-01, CAL-02
**Success Criteria** (what must be TRUE):
  1. Calendar tab shows a 90-day GitHub-style heatmap.
  2. Tapping a day shows the tasks completed that day.
**Plans**: 1 plan
- [x] 10-01-PLAN.md — Calendar Heatmap & Day Drill-Down

### Phase 11: Achievements System
**Goal**: Users earn and see achievements for streaks, volume, and speed-run behavior.
**Depends on**: Phase 10
**Requirements**: ACH-01, ACH-02, ACH-03, ACH-04, ACH-05
**Success Criteria** (what must be TRUE):
  1. User earns streak, volume, and speed-run achievements automatically.
  2. An in-app banner appears and auto-dismisses when an achievement unlocks.
  3. Achievements tab shows all 8 achievements with locked/unlocked state and progress.
**Plans**: 3 plans
- [x] 11-01-PLAN.md — Achievement Entity & DAO
- [x] 11-02-PLAN.md — Achievement Detection Logic in ViewModel
- [x] 11-03-PLAN.md — AchievementsScreen & Unlock Banner

</details>

### 🚧 v1.2 Polish & Power (In Progress)

**Milestone Goal:** Polish the UX with sounds, smarter task management, widget enhancements, and expanded notifications/achievements.

- [ ] **Phase 12: UX Quick Wins** - Enter key to add task, today-only task filter on Home tab, delete incomplete tasks.
- [ ] **Phase 13: Widget Overhaul** - Labeled sections, highlighted priorities, celebration banner, tap to open app.
- [ ] **Phase 14: Brain Dump Persistence & Sound** - Persist brain dump mid-session, ding sound on task completion.
- [ ] **Phase 15: Calendar Redesign** - Redesign calendar screen for better visual clarity and readability.
- [ ] **Phase 16: Push Notifications** - System push notification on achievement unlock.
- [ ] **Phase 17: New Achievements** - Four new achievements: Perfect Week, Early Bird, Night Owl, Comeback Kid.

## Phase Details

### Phase 12: UX Quick Wins
**Goal**: Users interact with the Today view more fluidly — typing, filtering, and deleting work as expected.
**Depends on**: Phase 11
**Requirements**: UX-03, UX-05, TASK-01
**Success Criteria** (what must be TRUE):
  1. User can press Enter/Return in the brain dump text field to submit a new task without tapping the button.
  2. Home tab shows only tasks completed today — tasks from previous days are not visible.
  3. User can delete an incomplete task from the Today view and it is removed immediately.
**Plans**: 2 plans
- [ ] 12-01-PLAN.md � Enter key submission in brain dump TextField (UX-03)
- [ ] 12-02-PLAN.md � Today-only completed task filter and delete incomplete tasks (UX-05, TASK-01)

### Phase 13: Widget Overhaul
**Goal**: The home screen widget is visually restructured to clearly distinguish priorities from brain dump tasks, celebrate completion, and launch the app on tap.
**Depends on**: Phase 12
**Requirements**: WIDG-06, WIDG-07, WIDG-08, WIDG-09
**Success Criteria** (what must be TRUE):
  1. Widget displays two labeled sections: "Priority" (top) and "Brain Dump" (bottom).
  2. Priority task rows are visually larger and use a highlighted style compared to brain dump rows.
  3. When all 3 priority tasks are done, the widget shows a static celebration banner in place of the task list.
  4. Tapping the widget (anywhere outside interactive rows) opens the ZenStack app.
**Plans**: TBD

### Phase 14: Brain Dump Persistence & Sound
**Goal**: Users never lose brain dump work to accidental app closure, and completing tasks is satisfying to hear.
**Depends on**: Phase 12
**Requirements**: TASK-02, WIDG-03
**Success Criteria** (what must be TRUE):
  1. Brain dump tasks entered during setup are still present when the app is reopened after being closed mid-session.
  2. A low-latency "ding" sound plays when a task is marked complete (SoundPool).
  3. Sound respects the device's silent/DND state and does not play when muted.
**Plans**: TBD

### Phase 15: Calendar Redesign
**Goal**: The calendar screen is visually clear, fits the screen without overflow, and is comfortable to read.
**Depends on**: Phase 12
**Requirements**: UX-04
**Success Criteria** (what must be TRUE):
  1. Calendar screen displays without overflow or clipping on standard phone screen sizes.
  2. Activity heatmap cells and day drill-down text are legible at default font sizes.
  3. The redesigned layout feels consistent with the rest of the app's visual language.
**Plans**: TBD

### Phase 16: Push Notifications
**Goal**: Users receive a system push notification each time they unlock a new achievement.
**Depends on**: Phase 11
**Requirements**: NOTF-01
**Success Criteria** (what must be TRUE):
  1. On first use, the app requests POST_NOTIFICATIONS permission from the user.
  2. When an achievement is unlocked, a system notification appears in the notification shade with the achievement name.
  3. Tapping the notification opens the Achievements tab in the app.
**Plans**: TBD

### Phase 17: New Achievements
**Goal**: Four new behavioral achievements are available to unlock, rewarding consistency, timing, and resilience.
**Depends on**: Phase 16
**Requirements**: ACH-06, ACH-07, ACH-08, ACH-09
**Success Criteria** (what must be TRUE):
  1. "Perfect Week" unlocks when the user completes all 3 priorities every day for 7 consecutive days.
  2. "Early Bird" unlocks when the user completes all 3 priorities before noon on any day.
  3. "Night Owl" unlocks when the user completes all 3 priorities after 9pm on any day.
  4. "Comeback Kid" unlocks when the user returns after a 7+ day gap and completes a full session.
  5. All 4 new achievements appear in the Achievements tab with correct locked/unlocked state and progress indicators.
**Plans**: TBD

## Progress

**Execution Order:**
Phases execute in numeric order: 12 → 13 → 14 → 15 → 16 → 17

| Phase | Milestone | Plans Complete | Status | Completed |
|-------|-----------|----------------|--------|-----------|
| 1. Foundation & Data Architecture | v1.0 | 4/4 | Complete | 2026-03-23 |
| 2. The Core Loop | v1.0 | 3/3 | Complete | 2026-03-23 |
| 3. Glance Widget Integration | v1.0 | 3/3 | Complete | 2026-03-23 |
| 4. Frictionless Access (Back Tap) | v1.0 | 3/3 | Complete | 2026-03-23 |
| 7. Stability & Data Integrity | v1.1 | 3/3 | Complete | 2026-03-24 |
| 8. Navigation Shell & Confetti | v1.1 | 2/2 | Complete | 2026-03-24 |
| 9. Scrollable Widget & Completion History | v1.1 | 2/2 | Complete | 2026-03-24 |
| 10. Calendar Heatmap | v1.1 | 1/1 | Complete | 2026-03-24 |
| 11. Achievements System | v1.1 | 3/3 | Complete | 2026-03-24 |
| 12. UX Quick Wins | v1.2 | 0/2 | Not started | - |
| 13. Widget Overhaul | v1.2 | 0/TBD | Not started | - |
| 14. Brain Dump Persistence & Sound | v1.2 | 0/TBD | Not started | - |
| 15. Calendar Redesign | v1.2 | 0/TBD | Not started | - |
| 16. Push Notifications | v1.2 | 0/TBD | Not started | - |
| 17. New Achievements | v1.2 | 0/TBD | Not started | - |
