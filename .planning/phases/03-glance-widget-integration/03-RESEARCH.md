# Phase 03: Glance Widget Integration - Research

**Researched:** 2026-03-23
**Domain:** Android Home Screen Widgets (Glance 1.2)
**Confidence:** HIGH

## Summary
Phase 3 focuses on creating a reactive, interactive widget using Glance 1.2. The research confirms that Glance 1.2 (currently in RC/Beta) introduces a session-based update mechanism via `provideGlance` which simplifies asynchronous data fetching from Room. Deep linking and custom sound feedback are fully supported through explicit Intents and the MediaPlayer API within Glance's `ActionCallback`.

**Primary recommendation:** Use `provideGlance` with `collectAsState` for Room data, and trigger global updates using `updateAll(context)` in DAOs/Repositories.

## User Constraints (from CONTEXT.md)
### Locked Decisions
- Flat Zen Palette (Glance-optimized).
- Visual Progress Bar (using Glance primitives).
- Deep Link to CurrentTasksView on widget background tap.
- Use custom "Zen" success sound asset.
- Explore Glance 1.2 state-based update mechanisms.

## Standard Stack
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| androidx.glance:glance-appwidget | 1.1.0 (or 1.2.0-rc01) | Widget Framework | Modern RemoteViews wrapper |
| androidx.glance:glance-material3 | 1.1.0 | Styling | Material 3 integration for widgets |
| Room | 2.6.1+ | Data Source | Multi-instance invalidation for widget sync |

## Architecture Patterns
### Room-to-Glance Synchronization
1. **The Passive View Pattern:** The widget should not hold its own state. It reflects the Room database.
2. **Reactive Composition:** Use `provideGlance` to start a session. Inside `provideContent`, collect Room `Flow` as state.
3. **Manual Invalidation:** Since widgets are cross-process, call `ZenStackWidget().updateAll(context)` whenever Room data changes (e.g., in `TaskRepository`).

## Visual Progress Bar
Use `LinearProgressIndicator` from `androidx.glance.appwidget`.
- **Height/Thickness:** Use `GlanceModifier.height(dp)`.
- **Rounded Corners:** Use `GlanceModifier.cornerRadius(dp)`. Note: Effective clipping requires API 31+. For older versions, the background is rounded but the indicator may overflow.
- **Custom Colors:** Use `color` and `backgroundColor` parameters with `ColorProvider`.

## Deep Linking
Use `actionStartActivity` with an **explicit** Intent.
```kotlin
val intent = Intent(context, MainActivity::class.java).apply {
    action = Intent.ACTION_VIEW
    data = Uri.parse("zenstack://current-tasks")
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
}
GlanceModifier.clickable(actionStartActivity(intent))
```
**Manifest:** `MainActivity` must have an `<intent-filter>` for the `zenstack` scheme.

## Custom Sound Feedback
In the `ToggleTaskCallback` (ActionCallback):
```kotlin
private fun playSuccessSound(context: Context) {
    val mp = MediaPlayer.create(context, R.raw.zen_success)
    mp.setOnCompletionListener { it.release() }
    mp.start()
}
```
*Note: `ActionCallback.onAction` is a suspend function; it is safe to perform this initialization there.*

## Common Pitfalls
- **Update Throttling:** Glance has a session lock (~45-50s). Frequent `updateAll` calls might be ignored or delayed.
- **Implicit Intent Blocking:** Android 14+ blocks implicit intents in PendingIntents. Always use `setComponent` or `setClass` for `MainActivity` targets.
- **Resource Constraints:** Widgets have a strict memory limit. Avoid large images or complex layouts.

## Code Examples
### Reactive Room Collection
```kotlin
override suspend fun provideGlance(context: Context, id: GlanceId) {
    val dao = AppDatabase.getDatabase(context).taskDao()
    provideContent {
        val tasks by dao.getPriorityTasks().collectAsState(initial = emptyList())
        // Render UI
    }
}
```

## Environment Availability
| Dependency | Required By | Available | Version |
|------------|------------|-----------|---------|
| Glance | Widget UI | ✓ | 1.1.1 (Upgrade to 1.2.0 recommended) |
| Room | Data Sync | ✓ | 2.6.1 |
| MediaPlayer | Success Sound | ✓ | System Native |

## Validation Architecture
- **Framework:** Compose UI Test (Glance-appwidget-testing)
- **Requirements → Test Map:**
    - WIDG-01: Verify `LinearProgressIndicator` presence in widget snapshot.
    - WIDG-02: `actionRunCallback` triggers Room update (tested via Mockito/FakeDao).
