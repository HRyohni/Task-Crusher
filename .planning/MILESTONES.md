# Milestones

## v1.1 Engagement (Shipped: 2026-03-24)

**Phases:** 7–11 (5 phases) · **Plans:** 12 · **Files changed:** 45 · **Net insertions:** ~4,900 lines

**Key accomplishments:**
- Data integrity hardened — Room safe migrations (v2→v3→v4→v5), `exportSchema=true`, session state reset prevents duplicate insertions
- Battery optimized — Accelerometer 12× reduction (SENSOR_DELAY_UI → SENSOR_DELAY_NORMAL); widget uses targeted Repository queries
- Bottom navigation shell — Today / Calendar / Achievements tabs; existing session UI fully preserved
- Confetti celebration — Full-screen konfetti-compose overlay on all-3-priorities-complete
- Scrollable Glance widget — LazyColumn replaces hard-capped Column; all priority + brain dump tasks visible
- Calendar heatmap — 90-day GitHub-style heatmap (4-level color scale) with tappable day drill-down
- Achievements system — 8 achievements (streaks, volume, speed runs) with persistent DB tracking and in-app unlock pop-up

---

