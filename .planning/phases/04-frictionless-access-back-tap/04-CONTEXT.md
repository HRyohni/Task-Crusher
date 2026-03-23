# Phase 04: Frictionless Access (Back Tap) - Context

**Gathered:** March 23, 2026
**Status:** Ready for planning

<domain>
## Phase Boundary

Establishing "zero-click" access to ZenStack through physical gesture integration. This phase focuses on implementing an Android Accessibility Service to detect "Back Tap" (double-tap on the back of the device) and providing a one-time onboarding guide to help users configure this feature.

</domain>

<decisions>
## Implementation Decisions

### GEST-01: Back Tap Implementation
- **Accessibility Service:** Implement a custom `AccessibilityService` to listen for system-level gestures.
- **Double-Tap Detection:** Specifically focus on detecting the "double-tap" gesture to trigger app launch.
- **Target Destination:** Upon successful gesture detection, the app must launch directly into the `CurrentTasksView` (the screen showing both Priority and Brain Dump items).

### GEST-02: Onboarding Guide
- **One-Time Experience:** The "How-To" guide for setting up Back Tap/Quick Tap should be presented as a one-time walkthrough or modal during the initial setup flow (not as a persistent card).
- **No Status Indicator:** Do NOT add a "Quick Access Active" visual indicator to the main UI once the gesture is configured; keep the interface clean and minimalist.

</decisions>

<canonical_refs>
## Canonical References

### Project Core
- `.planning/PROJECT.md` — Vision and Core Value.
- `.planning/REQUIREMENTS.md` — GEST-01, GEST-02.
- `.planning/ROADMAP.md` — Phase 4 Goal and Success Criteria.

### Previous Phase Integration
- `.planning/phases/03-glance-widget-integration/03-CONTEXT.md` — Reference for navigation to `CurrentTasksView`.
- `app/src/main/java/hr/fipu/organizationtool/MainActivity.kt` — Entry point for handling launch intents from the service.

</canonical_refs>

<code_context>
## Codebase Insights

### Integration Points
- `AndroidManifest.xml` — Requires registration of the `AccessibilityService` and specific permissions (`android.permission.BIND_ACCESSIBILITY_SERVICE`).
- `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt` — Needs to integrate the one-time onboarding guide into the `SetupFlow`.

### New Components
- `ZenStackAccessibilityService.kt` — New service to handle gesture detection.
- `res/xml/accessibility_service_config.xml` — Configuration for the accessibility service.

</code_context>

<deferred>
## Deferred Ideas
- **Custom Gesture Configuration:** Allowing users to map "Triple Tap" or other gestures is deferred to future versions.
- **System Settings Deep Linking:** Navigating users directly to the specific "Quick Tap" system setting page (which varies by OEM) is deferred; the guide will provide general instructions.

</deferred>

---

*Phase: 04-frictionless-access-back-tap*
*Context gathered: March 23, 2026*
