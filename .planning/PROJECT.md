# ZenStack (Working Title)

A minimalist productivity tool designed to clear mental clutter and focus on what matters most through a guided 3-step setup and a high-interactivity home screen widget.

## Core Value
**Focus through Frictionless Intent:** Minimize the gap between a thought and its capture, then force a choice to prevent paralysis.

## Context
ZenStack addresses "choice paralysis" by guiding users through a "Session" that resets their focus. It prioritizes the Home Screen (via Glance Widget) and quick access (via Back Tap) as the primary interaction points.

## Requirements

### Validated
- ✓ **CORE-01**: Android (Kotlin/Compose) project structure - *existing*
- ✓ **DATA-01**: Room Database persistence for tasks - *existing*
- ✓ **WIDG-01**: Initial Glance-based widget implementation - *existing*

### Active
- [ ] **DUMP-01**: Brain Dump Interface - Clean UI with prominent text input for rapid task entry.
- [ ] **DUMP-02**: Tactile Tags - Tasks appear as interactive "bubbles" that can be tapped to edit or deleted with an 'X'.
- [ ] **PRIO-01**: The Power 3 - Selection screen where users MUST choose exactly 3 priorities from the dump.
- [ ] **PRIO-02**: Choice Constraint - Logic to prevent selecting more than 3 items (dimming/warning).
- [ ] **CONF-01**: Session Save - Final summary that updates the local DB and triggers a widget refresh.
- [ ] **WIDG-02**: Interactive Widget - Progress tracker (1/3 done) and ability to check off tasks directly from home screen.
- [ ] **WIDG-03**: Audio Feedback - A satisfying "success" sound plays when a task is completed via the widget.
- [ ] **GEST-01**: Back Tap Support - Implementation of an Accessibility Service or Shortcut to launch the app via double-tap.
- [ ] **GEST-02**: Onboarding Guide - In-app "How-To" for setting up system-level Back Tap gestures.

### Out of Scope
- Cloud Sync (MVP is local-only for privacy/speed)
- Multiple Lists (One focus session at a time)
- Recurring Tasks (Focus is on immediate "now" tasks)

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| **Tech Stack** | Modern Kotlin/Compose/Glance | Confirmed |
| **Back Tap** | Accessibility Service + User Guide | Pending |
| **Tactile Dump** | Physics-based or animated "bubbles" for tactile feel | Pending |
| **Audio** | Integrated "Success" sound (Standard GSD choice) | Pending |

## Current Milestone: v1.1 Engagement

**Goal:** Transform ZenStack from a focused task tool into a rewarding, habit-forming app with richer UX, engagement loops, and data integrity.

**Target features:**
- Critical bug fixes (DB migrations, widget performance, sensor battery drain)
- Confetti explosion when all 3 priority tasks are completed
- Scrollable widget showing all tasks (priority + brain dump)
- Deep-link button directly to accessibility settings for double-tap setup
- Bottom navigation bar (Today, Calendar, Achievements)
- Calendar tab — GitHub-style activity heatmap + tappable day detail
- Achievements tab — streaks, volume milestones, speed runs with pop-up notifications

## Evolution
This document evolves at phase transitions and milestone boundaries.

---
*Last updated: March 23, 2026 after v1.1 milestone started*
