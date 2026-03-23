# Phase 01: Foundation & Data Architecture - Context

**Gathered:** March 23, 2026
**Status:** Ready for planning

<domain>
## Phase Boundary

Establishing the core technical foundation for ZenStack, including the upgrade to Kotlin 2.1 (K2), implementation of Material 3 Expressive (1.4.0) with a custom "Zen" theme, and setting up the Room KMP repository as the app's single source of truth.

</domain>

<decisions>
## Implementation Decisions

### M3 Expressive Styling
- **D-01: Zen Palette (Fixed)** — Use a curated grayscale palette with a single soft accent color to maintain a focused, minimalist environment.
- **D-02: Physical Shadows** — Utilize subtle physical shadows to emphasize the "tactile" nature of bubbles and tags, moving beyond flat tonal elevation.
- **D-03: Modern Sans-Serif** — Use a clean, geometric sans-serif (e.g., Inter or Roboto) to achieve a fast, efficient "Stack" vibe.
- **D-04: Global Motion Physics** — Define standard Damping and Stiffness constants within the global theme to ensure all tactile animations feel consistent across the app.

### Claude's Discretion
- **Dependency Management:** Claude has the discretion to choose between Koin 4.0 or manual DI, though Koin 4.0 is recommended for KMP consistency.
- **Data Migration:** Claude can decide whether to perform a clean Room migration or start a fresh schema for ZenStack.
- **Testing Setup:** Claude has the discretion to set up the KMP-native testing architecture for the Repository in this phase.

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Core
- `.planning/PROJECT.md` — Vision and Core Value.
- `.planning/REQUIREMENTS.md` — v1 Requirements (CORE-01, CORE-02).
- `.planning/ROADMAP.md` — Phase 1 Goal and Success Criteria.

### Technical Research
- `.planning/research/STACK.md` — Kotlin 2.1, M3 Expressive, and Glance 1.2 versions.
- `.planning/research/ARCHITECTURE.md` — Room-to-Glance data flow patterns.
- `.planning/research/PITFALLS.md` — Warnings on Glance desync and process separation.

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `app/src/main/java/hr/fipu/organizationtool/data/` — Existing Room setup can serve as a structural reference for the ZenStack schema.

### Established Patterns
- **UDF with ViewModel:** The existing `TaskViewModel.kt` uses a standard Android ViewModel pattern that will be evolved into the "Direct State" pattern researched.

### Integration Points
- `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt` — The main entry point where the new M3 Expressive theme will be applied.

</code_context>

<specifics>
## Specific Ideas
- The "Zen" palette should feel "calm but active" — think whites, soft grays, and a deep indigo or slate for the accent.

</specifics>

<deferred>
## Deferred Ideas
- **Tactile Physics Tuning:** While constants are defined now, the actual physics-based "bubbles" are deferred to Phase 5.
- **Glance Interactivity:** Full widget interactive loops are deferred to Phase 3.

</deferred>

---

*Phase: 01-foundation-data-architecture*
*Context gathered: March 23, 2026*
