---
phase: 15-calendar-redesign
plan: "01"
status: complete
date: "2026-03-25"
duration_min: ~5
tasks_completed: 2
files_modified: 1
---

# Phase 15 Plan 01: Calendar Screen Redesign Summary

## What Was Built

Six visual fixes applied to CalendarScreen.kt (UX-04):

1. **Proportional cells:** `weight(1f).aspectRatio(1f)` replaces fixed `size(32.dp)` — cells now scale to available width on any screen size, eliminating overflow and gaps.
2. **Smaller header:** `titleLarge` replaces `headlineMedium` on the "Activity" Text — less visually dominant, matches app tone.
3. **Today indicator:** `ZenIndigo` border (1.5dp) applied to current date cell via `.then(if (date == today) Modifier.border(...) else Modifier)` — distinguishes today at a glance.
4. **Legend row:** "Less ... More" row with four `heatmapColor` swatches (counts 0/1/2/4) inserted below the heatmap grid — provides color scale context.
5. **Conditional divider:** `HorizontalDivider` wrapped in `if (selectedDay != null)` — no divider until a day is tapped.
6. **Left-aligned detail panel:** Removed `contentAlignment = Alignment.Center` from detail `Box`, added `padding(horizontal = 16.dp, vertical = 8.dp)` to outer Box; reduced inner `Surface` padding from `horizontal = 16.dp` to 0 to avoid double-padding.

New import added: `import androidx.compose.foundation.border`

## Files Modified

- `app/src/main/java/hr/fipu/organizationtool/ui/CalendarScreen.kt`

## Commits

| Task | Description | Hash |
|------|-------------|------|
| 1 | feat(calendar): redesign CalendarScreen for better fit and readability (UX-04) | 4475c91 |

## Deviations from Plan

None — plan executed exactly as written.

## Requirements Satisfied

- UX-04: Calendar screen redesigned for better visual clarity, proportional sizing, today indicator, and contextual color legend.

## Self-Check: PASSED

- `size(32.dp)` in CalendarScreen.kt: 0 matches
- `aspectRatio(1f)` in CalendarScreen.kt: 3 matches (date cell, empty null cell, trailing filler)
- `border(` in CalendarScreen.kt: 1 match (today indicator)
- `titleLarge` in CalendarScreen.kt: 1 code usage (+ 1 comment)
- `HorizontalDivider` wrapped in `if (selectedDay != null)`: confirmed at line 143-147
- Commit 4475c91 exists: confirmed
