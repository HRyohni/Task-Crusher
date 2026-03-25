# Requirements: ZenStack v1.2 Polish & Power

**Defined:** 2026-03-25
**Core Value:** Focus through Frictionless Intent: Minimize the gap between a thought and its capture, then force a choice to prevent paralysis.

## v1.2 Requirements

### Widget Enhancements

- [ ] **WIDG-03**: User hears a "ding" sound when a task is completed (SoundPool, low-latency)
- [ ] **WIDG-06**: Widget is divided into two labeled sections: "Priority" and "Brain Dump"
- [ ] **WIDG-07**: Priority task rows are visually larger and highlighted compared to brain dump rows
- [ ] **WIDG-08**: Widget shows a static celebration banner when all 3 priority tasks are complete
- [ ] **WIDG-09**: User can tap the widget to launch the app

### UX Polish

- [ ] **UX-03**: User can press Enter/Return in brain dump text field to submit a new task
- [ ] **UX-04**: Calendar screen is visually redesigned for better readability and fit
- [ ] **UX-05**: Home tab shows only tasks completed today, not from previous days

### Task Management

- [ ] **TASK-01**: User can delete an incomplete (undone) task from the Today view
- [ ] **TASK-02**: Brain dump tasks entered during setup persist if the app is closed mid-session and reopened

### Notifications

- [ ] **NOTF-01**: System push notification is sent when a new achievement is unlocked (requires POST_NOTIFICATIONS permission)

### Achievements

- [ ] **ACH-06**: "Perfect Week" achievement unlocked when user completes all 3 priorities every day for 7 consecutive days
- [ ] **ACH-07**: "Early Bird" achievement unlocked when user completes all 3 priorities before noon
- [ ] **ACH-08**: "Night Owl" achievement unlocked when user completes all 3 priorities after 9pm
- [ ] **ACH-09**: "Comeback Kid" achievement unlocked when user returns after a 7+ day gap and completes a full session

## Future Requirements

### Task Management

- **TASK-03**: User can reorder brain dump tasks by drag-and-drop
- **TASK-04**: User can edit a task name after it has been added

### Widget

- **WIDG-10**: Widget shows task count badge on app icon

## Out of Scope

| Feature | Reason |
|---------|--------|
| Cloud Sync | MVP is local-only for privacy/speed |
| Multiple Lists | One focus session at a time |
| Recurring Tasks | Focus is on immediate "now" tasks |
| Non-Android Platforms | Platform-native focus |
| In-widget animations / confetti | Glance does not support animations |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| WIDG-03 | — | Pending |
| WIDG-06 | — | Pending |
| WIDG-07 | — | Pending |
| WIDG-08 | — | Pending |
| WIDG-09 | — | Pending |
| UX-03 | — | Pending |
| UX-04 | — | Pending |
| UX-05 | — | Pending |
| TASK-01 | — | Pending |
| TASK-02 | — | Pending |
| NOTF-01 | — | Pending |
| ACH-06 | — | Pending |
| ACH-07 | — | Pending |
| ACH-08 | — | Pending |
| ACH-09 | — | Pending |

**Coverage:**
- v1.2 requirements: 15 total
- Mapped to phases: 0
- Unmapped: 15 ⚠️

---
*Requirements defined: 2026-03-25*
*Last updated: 2026-03-25 after initial definition*
