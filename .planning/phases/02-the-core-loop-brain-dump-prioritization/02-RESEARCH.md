# Phase 02: The Core Loop - Research

**Researched:** May 24, 2024
**Domain:** Jetpack Compose UI/UX & Room Persistence
**Confidence:** HIGH

## Summary
Phase 2 focuses on the "Brain Dump" and "Power 3" prioritization loop. Research confirms that a persistent header for input combined with tactile haptic and visual feedback creates the requested "frictionless" and "tactile" experience. Implementation will leverage existing `ZenTheme` physics and Room-based state management.

**Primary recommendation:** Use `HapticFeedbackType.Confirm` for selection limits and a fixed `OutlinedTextField` with `ImeAction.Done` for rapid entry.

## User Constraints (from CONTEXT.md)

### Locked Decisions
- **Persistent Header:** Top input field that adds tasks to the list.
- **Haptic Tick:** `HapticFeedbackType.TextHandleMove` on task capture.
- **Tactile Elevation:** Selected items use `Modifier.zenShadow()`.
- **Pop Motion:** Spring-based scale animation on selection.
- **Limit Feedback:** Double-vibration and "Select only 3" popup if 4th selected.
- **Persistence:** Suggest uncompleted priorities and recent dump items for new sessions.

### the agent's Discretion
- Minimalist UI for the "Select only 3" warning (Pill-shaped popup vs Toast).
- Specific Room query structure for session suggestions.

### Deferred Ideas (OUT OF SCOPE)
- Physics-based Bubbles (Deferred to Phase 5).
- Archive History View.

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Compose Material3 | 1.4.0 | UI Components | Project standard |
| Room | 2.7.0 | Persistence | Project standard |
| Koin | 4.0.0 | DI | Project standard |

## Architecture Patterns

### Recommended Project Structure
- No changes to structure; extending `TaskViewModel.kt` and `TaskDao.kt`.

### Pattern 1: Frictionless Batch Entry
Use a fixed `Column` or `Box` wrapper around `LazyColumn` where the header is outside the scrollable area to remain persistent.
- Use `KeyboardOptions(imeAction = ImeAction.Done)` to allow rapid entry without closing the keyboard.
- Clear text and trigger haptic on `onDone`.

### Anti-Patterns to Avoid
- **Keyboard Hiding:** Don't let the keyboard close between entries in a brain dump session.
- **Intrusive Toasts:** Standard Android Toasts are too "system-like"; use a custom pill popup for the Zen aesthetic.

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Spring Physics | Custom interpolators | `animate*AsState` | Integrated with Compose state |
| Persistence | SharedPrefs/JSON | Room | Already integrated, better query support |

## Common Pitfalls

### Pitfall 1: Haptic Compatibility
**What goes wrong:** `HapticFeedbackType.Confirm` might not provide a double-pulse on all devices/API levels.
**How to avoid:** Use a helper that falls back to `Vibrator` API with a waveform `[0, 50, 100, 50]` if precision is required.

## Code Examples

### Spring Pop Effect
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isSelected) 1.05f else 1f,
    animationSpec = zenSpring(),
    label = "PopAnimation"
)
Modifier.scale(scale).zenShadow(elevation = if (isSelected) 8.dp else 0.dp)
```

### Pill Warning Popup
```kotlin
@Composable
fun WarningPill(message: String, visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer) {
            Text(message, modifier = Modifier.padding(8.dp))
        }
    }
}
```

## Environment Availability
- Room, Compose, and Haptic APIs are all available in the current environment.

## Validation Architecture
- **Framework:** JUnit 4 + Espresso/Compose Test.
- **REQ-DUMP-01:** Test `addTask` updates state and list.
- **REQ-PRIO-01/02:** Test selection logic and limit (3).

## Sources
- Android Official Docs (HapticFeedbackType, Room Query)
- Jetpack Compose Samples (Persistent Header patterns)
