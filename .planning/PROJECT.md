# ZenStack (Working Title)

A minimalist Android productivity app that guides users through a focused "brain dump + Power 3" session, surfaces their top 3 priorities on the home screen widget, and rewards consistent use through streaks, volume milestones, and speed-run achievements.

## Core Value
**Focus through Frictionless Intent:** Minimize the gap between a thought and its capture, then force a choice to prevent paralysis.

## Context
ZenStack addresses "choice paralysis" by guiding users through a session that resets their focus. The Home Screen widget (now scrollable, showing all tasks) and Back Tap gesture (deep-link to accessibility settings) are the primary interaction points. v1.1 added engagement loops: confetti, calendar history, and achievements.

## Requirements

### Validated
- ✓ **CORE-01**: Android (Kotlin/Compose) project structure — *existing*
- ✓ **DATA-01**: Room Database persistence for tasks — *existing*
- ✓ **WIDG-01**: Initial Glance-based widget — *existing*
- ✓ **GEST-01**: Back Tap Gesture via Accessibility Service — v1.0 (Phase 4)
- ✓ **GEST-02**: Onboarding guide for back-tap setup — v1.0 (Phase 4)
- ✓ **BUG-01**: Room safe migrations (no destructive fallback) — v1.1 (Phase 7)
- ✓ **BUG-02**: `hasSeenBackTapGuide` correct default on fresh install — v1.1 (Phase 7)
- ✓ **BUG-03**: `saveSession` upserts without duplicating suggested tasks — v1.1 (Phase 7)
- ✓ **BUG-04**: Room `exportSchema=true` for migration tracking — v1.1 (Phase 7)
- ✓ **PERF-01**: Widget uses targeted DAO queries via Repository — v1.1 (Phase 7)
- ✓ **PERF-02**: Accelerometer at SENSOR_DELAY_NORMAL (12× reduction) — v1.1 (Phase 7)
- ✓ **PERF-03**: Widget data through TaskRepository, not AppDatabase directly — v1.1 (Phase 7)
- ✓ **UX-02**: Deep-link button to Android Accessibility Settings — v1.1 (Phase 7)
- ✓ **NAV-01**: Bottom navigation bar (Today, Calendar, Achievements) — v1.1 (Phase 8)
- ✓ **NAV-02**: Today tab preserves existing session UI — v1.1 (Phase 8)
- ✓ **UX-01**: Confetti animation when all 3 priorities complete — v1.1 (Phase 8)
- ✓ **WIDG-04**: Widget scrollable, all priority tasks shown — v1.1 (Phase 9)
- ✓ **WIDG-05**: Brain dump tasks visible below priorities in widget — v1.1 (Phase 9)
- ✓ **CAL-03**: Task completion timestamp recorded for history queries — v1.1 (Phase 9)
- ✓ **CAL-01**: GitHub-style activity heatmap in Calendar tab — v1.1 (Phase 10)
- ✓ **CAL-02**: Tap day → see tasks completed that day — v1.1 (Phase 10)
- ✓ **ACH-01**: Daily streak tracking — v1.1 (Phase 11)
- ✓ **ACH-02**: Volume milestones (10/50/100 tasks) — v1.1 (Phase 11)
- ✓ **ACH-03**: Speed run achievement (all 3 in <1 hour) — v1.1 (Phase 11)
- ✓ **ACH-04**: In-app pop-up on achievement unlock — v1.1 (Phase 11)
- ✓ **ACH-05**: Achievements tab with locked/unlocked + progress — v1.1 (Phase 11)

### Active
- [ ] **DUMP-01**: Brain Dump Interface — clean UI with prominent text input (partially built)
- [ ] **DUMP-02**: Tactile Tags — tasks as animated bubbles with spring-based motion
- [ ] **DUMP-03**: Interaction Layer — tap to edit, 'X' to delete tags
- [ ] **PRIO-01**: Power 3 Selection Interface — pool of tactile tags for prioritization
- [ ] **PRIO-02**: Choice Constraint — strictly limit selection to 3 items
- [ ] **PRIO-03**: Visual Dimming — non-selected items dim when limit reached
- [ ] **WIDG-02**: Widget Action Loop — check off tasks from home screen, sync to Room
- [ ] **WIDG-03**: Success Audio — SoundPool low-latency "ding" on task completion

### Out of Scope
- Cloud Sync — MVP is local-only for privacy/speed
- Multiple Lists — one focus session at a time
- Recurring Tasks — focus is on immediate "now" tasks
- Non-Android Platforms — platform-native focus
- System Notifications for achievements — in-app overlay is sufficient

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| **Tech Stack** | Kotlin 2.1, Compose, Glance, Room, Koin | ✓ Good |
| **Back Tap** | Accessibility Service + User Guide | ✓ Good |
| **Tactile Dump** | Physics-based animated "bubbles" | — Pending (Phase 5) |
| **Audio** | SoundPool "ding" on task complete | — Pending (Phase 6) |
| **Room Migrations** | Explicit `Migration(N, N+1)` instead of destructive fallback | ✓ Good |
| **Sensor Rate** | SENSOR_DELAY_NORMAL (5Hz) for gesture detection | ✓ Good — 12× battery saving |
| **Konfetti** | `nl.dionsegijn:konfetti-compose 2.0.5` for confetti | ✓ Good |
| **Achievements storage** | Room table `achievements(id, unlockedAt)` with IGNORE conflict | ✓ Good — idempotent |
| **Calendar** | Pure Compose `LazyVerticalGrid`, no external library | ✓ Good |
| **In-app only notifications** | AnimatedVisibility banner, no system permissions needed | ✓ Good |

## Constraints

- **Platform:** Android only, minSdk 28 (safe for `java.time.*`)
- **Privacy:** Local-only data — no cloud sync, no analytics
- **Dependencies:** Minimal — prefer Compose primitives over external libraries

---
*Last updated: 2026-03-24 after v1.1 Engagement milestone*
