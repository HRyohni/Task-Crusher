# ZenStack v1 Requirements

## v1 Requirements

### CORE: Foundation & Data
- [ ] **CORE-01**: Setup Kotlin 2.1 (K2) and Material 3 Expressive (1.4.0) foundation.
- [ ] **CORE-02**: Room KMP Repository as Single Source of Truth for tasks and session state.

### DUMP: The Brain Dump
- [x] **DUMP-01**: "Void Input" - Minimalist, full-screen text entry for rapid task capture.
- [ ] **DUMP-02**: "Tactile Tags" - Visual representation of tasks as bubbles with spring-based motion (`AnchoredDraggable`).
- [ ] **DUMP-03**: Interaction Layer - Tap to edit, 'X' to delete tags in real-time.

### PRIO: The Power 3
- [ ] **PRIO-01**: Selection Interface - Display pool of tactile tags for prioritization.
- [ ] **PRIO-02**: Choice Constraint - Logic to strictly limit priority selection to 3 items.
- [ ] **PRIO-03**: Visual Dimming - Dynamic UI feedback for non-selected items when limit is reached.

### WIDG: The Glance Widget
- [ ] **WIDG-01**: Glance 1.2.0 Interactive Widget - Display Top 3 and "Progress Tracker" (e.g., 1/3 Done).
- [ ] **WIDG-02**: Widget Action Loop - Check off tasks directly from home screen with `ActionCallback` sync to Room.
- [ ] **WIDG-03**: Success Audio - `SoundPool` implementation for low-latency "ding" on task completion.

### GEST: Frictionless Access
- [x] **GEST-01**: Back Tap Gesture - Accessibility Service or Shortcut to launch ZenStack via double-tap.
- [x] **GEST-02**: Onboarding Guide - In-app visual "How-To" for system-level gesture mapping.

## v2 Requirements (Deferred)
- [ ] Cloud Sync & Multi-device Support
- [ ] Historical Focus Session Analytics
- [ ] Customizable Audio Themes

## Out of Scope
- Recurring Tasks (Focus is on the immediate "now")
- Complex Tagging/Categorization (Minimalism first)
- Non-Android Platforms (Platform-native focus)

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| CORE-01 | Phase 1 | Pending |
| CORE-02 | Phase 1 | Pending |
| DUMP-01 | Phase 2 | Complete |
| DUMP-02 | Phase 5 | Pending |
| DUMP-03 | Phase 5 | Pending |
| PRIO-01 | Phase 2 | Pending |
| PRIO-02 | Phase 2 | Pending |
| PRIO-03 | Phase 2 | Pending |
| WIDG-01 | Phase 3 | Pending |
| WIDG-02 | Phase 3 | Pending |
| WIDG-03 | Phase 6 | Pending |
| GEST-01 | Phase 4 | Complete |
| GEST-02 | Phase 4 | Complete |
