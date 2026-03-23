# Research Summary: ZenStack Architecture

**Domain:** Android Interactive Widget-first Productivity System
**Researched:** 2024-03-24
**Overall Confidence:** HIGH

## Executive Summary

ZenStack's architecture must bridge the gap between two asynchronous processes: the **Main App** (where "Brain Dump" sessions occur) and the **Glance Widget** (the primary interaction point). The core challenge is maintaining a synchronized state across these processes while providing low-latency feedback for "Back Tap" gestures and widget interactions.

Research confirms that a **Single Source of Truth (Room DB)** is the most robust pattern for this. The application will leverage **Jetpack Glance**'s state-driven model, where the `TaskRepository` acts as the orchestrator for both data persistence and cross-process UI updates. For friction-free access, an **Accessibility Service** monitoring accelerometer sensors will provide a "Back Tap" trigger, though it requires specific onboarding to bypass Android's "Restricted Settings" (Android 13+).

## Key Findings

**Stack:** Kotlin 2.0 / Compose / Glance 1.1 / Room (Standard modern Android stack).
**Architecture:** Unidirectional Data Flow (UDF) with Room-triggered widget refreshes (`updateAll`).
**Critical Pitfall:** Glance process separation can lead to stale UI if updates aren't manually triggered from the Repository.

## Implications for Roadmap

Based on research, the following phase structure is recommended:

1.  **Core Persistence & Widget Loop (Phases 1-3)**
    *   Addresses: **DUMP-01**, **PRIO-01/02**, **WIDG-02**.
    *   Rationale: Establishes the Room -> Repository -> Glance data flow early. This is the riskiest architectural part.
2.  **Frictionless Access (Phase 4)**
    *   Addresses: **GEST-01/02**.
    *   Avoids: Accessibility "Restricted Settings" pitfall by including onboarding.
3.  **Polish & Feedback (Phase 5)**
    *   Addresses: **WIDG-03** (Audio).
    *   Note: Use `SoundPool` for low-latency widget feedback.
4.  **Advanced UI (Phase 6)**
    *   Addresses: **DUMP-02** (Tactile Tags).
    *   Note: Defer complex physics-based animations to late-stage polish as they don't affect core data flow.

**Research flags for phases:**
-   Phase 4 (Back Tap): Needs validation on different hardware (sensor sensitivity) and Android versions.
-   Phase 2 (Glance Widget): Ensure the `SizeMode.Responsive` correctly handles different launcher grid sizes.

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | HIGH | Modern, well-documented, and already partially implemented. |
| Features | MEDIUM | "Tactile bubbles" in Compose might require significant animation tuning. |
| Architecture | HIGH | Standard UDF with Room + Glance is the industry-recommended pattern for 2024. |
| Pitfalls | MEDIUM | Back Tap implementation is highly device-dependent and may have false positives. |

## Gaps to Address

-   **Back Tap Sensitivity:** Need a phase to calibrate the accelerometer thresholds for the "double-tap" gesture.
-   **Widget Size Variations:** Need to test how the "Power 3" display scales on very small (2x2) vs. large (4x4) widgets.
-   **Audio Management:** Confirming if system "Do Not Disturb" settings should override the widget "success" sound.
