# Phase 04: Frictionless Access (Back Tap) - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-03-23
**Phase:** 04-frictionless-access-back-tap
**Areas discussed:** Implementation Strategy, Onboarding UX, Gesture Destination, UI Polish.

---

## 1. Implementation Strategy

| Option | Description | Selected |
|--------|-------------|----------|
| **Accessibility Service** | Detects double-tap natively (requires permission). | ✓ |
| **System Shortcut Only** | Relies on Pixel/Samsung built-in settings. | |

**Note:** User chose the Accessibility Service approach for native detection capabilities.

## 2. Onboarding Guide Placement

| Option | Description | Selected |
|--------|-------------|----------|
| **One-Time Walkthrough** | Guided setup during initial app use. | ✓ |
| **Persistent Help Card** | Help card always visible in tasks view. | |

**Note:** User preferred a "one-time" experience to keep the main interface clutter-free.

## 3. Back Tap Destination

| Option | Description | Selected |
|--------|-------------|----------|
| **Current Tasks View** | See progress and current priorities. | ✓ |
| **Brain Dump View** | Capture new thoughts immediately. | |

**Note:** Double-tapping will launch the app directly into the `CurrentTasksView`.

## 4. Visual Cues

| Option | Description | Selected |
|--------|-------------|----------|
| **Active Indicator** | Small UI element showing feature is on. | |
| **No Indicator** | Keep UI minimalist. | ✓ |

**Note:** User explicitly declined a status indicator to maintain minimalism.
