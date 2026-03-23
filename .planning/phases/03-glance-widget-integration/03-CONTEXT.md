# Phase 03: Glance Widget Integration - Context

**Gathered:** March 23, 2026
**Status:** Ready for planning

<domain>
## Phase Boundary

Bridging the application and the Android home screen through an interactive Glance widget. This phase focuses on visual alignment with the Zen design system, implementing a visual progress tracker, deep-linking into the core task view, and integrating a custom sensory "success" sound.

</domain>

<decisions>
## Implementation Decisions

### Visual Identity
- **Flat Zen Palette:** Since Glance has limited support for complex shadows, use a "flat" representation of the Zen palette (clean grays, whites, and the Indigo accent color).
- **Visual Progress Bar:** Implement a minimalist visual progress bar using Glance layout primitives (e.g., nested `Box` elements or a thin `LinearProgressIndicator` if available/reliable in Glance) instead of just text tracker.

### Interactivity & UX
- **Deep Link Destination:** Tapping the widget background (non-action area) must lead the user to the `CurrentTasksView` (the page displaying both Priority and Brain Dump items).
- **Glance 1.2 Exploration:** Investigate and utilize Glance 1.2's state-based update mechanisms for potentially smoother cross-process synchronization compared to manual `updateAll` calls.

### Sensory Feedback
- **Zen Success Sound:** Replace the system default notification sound with a custom "Zen" audio asset (e.g., a soft bell or meditative "ding") for task completion.

</decisions>

<canonical_refs>
## Canonical References

### Project Core
- `.planning/PROJECT.md` — Vision and Core Value.
- `.planning/REQUIREMENTS.md` — WIDG-01, WIDG-02.
- `.planning/ROADMAP.md` — Phase 3 Goal and Success Criteria.

### Technical Foundation
- `.planning/phases/01-foundation-data-architecture/01-CONTEXT.md` — Zen Palette definitions.
- `app/src/main/java/hr/fipu/organizationtool/data/AppDatabase.kt` — Multi-instance invalidation setup (critical for widget sync).
- `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt` — Existing widget structure.

</canonical_refs>

<code_context>
## Codebase Insights

### Integration Points
- `ZenStackWidget.kt` — Needs to be updated with the visual progress bar and deep linking.
- `TaskActionHandler.kt` — Needs to trigger the new custom Zen sound asset.
- `MainActivity.kt` — Needs to handle the deep link intent to land on the correct screen.

### Assets Needed
- `res/raw/zen_success.mp3` (or similar) — A custom sound asset needs to be sourced/placeholder-created.

</code_context>

<deferred>
## Deferred Ideas
- **Widget Configuration Screen:** Allowing users to customize widget colors/transparency is deferred.
- **Multiple Widget Sizes:** Initial focus is on a standard 4x2 or 2x2 layout; complex resizing logic is deferred.

</deferred>

---

*Phase: 03-glance-widget-integration*
*Context gathered: March 23, 2026*
