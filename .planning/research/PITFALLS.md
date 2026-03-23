# Domain Pitfalls: ZenStack

**Domain:** Minimalist Productivity
**Researched:** May 2025
**Overall Confidence:** HIGH

## Critical Pitfalls

### Pitfall 1: Widget Staleness
**What goes wrong:** User completes task in widget, but UI doesn't reflect it instantly; or database updates don't trigger widget refresh.
**Why it happens:** Glance widgets are not "live" views; they require manual update triggers via `updateAll()`.
**Consequences:** Users feel the app is broken or laggy.
**Prevention:** Always trigger `ZenStackWidget().updateAll(context)` in the `TaskRepository` or `TaskActionHandler`.

### Pitfall 2: Accessibility Overreach (Back Tap)
**What goes wrong:** Requesting too many Accessibility permissions or having the service run in the background causing battery drain.
**Why it happens:** Monitoring all `onAccessibilityEvent` can be expensive.
**Consequences:** System may kill the app or show a "Heavy Battery Usage" warning.
**Prevention:** Set the `accessibilityEventTypes` to only `TYPE_WINDOW_STATE_CHANGED` and use a `packageNames` filter for the system launcher if possible.

## Moderate Pitfalls

### Pitfall 1: "Power 3" Selection Logic
**What goes wrong:** The UI allows selecting 4 items for a split second, or the "selection full" state is confusing.
**Why it happens:** Race conditions between UI state and selection logic in `viewModel`.
**Prevention:** Use a single source of truth (State) and use `viewModelScope` to atomicize selection updates.

### Pitfall 2: Animation Performance (Bubbles)
**What goes wrong:** 100+ tasks in a "Brain Dump" causing frame drops on older devices.
**Why it happens:** Too many concurrent `Animatable` or `SpringSpec` instances.
**Prevention:** Limit the number of "Active" bubbles (visible on screen) and use `LazyLayout` for large lists if needed.

## Phase-Specific Warnings

| Phase Topic | Likely Pitfall | Mitigation |
|-------------|---------------|------------|
| **Interactive Widget** | `ActionCallback` context leaks. | Use the provided `ActionParameters` instead of long-lived context references. |
| **Tactile Bubbles** | Physics feeling "mushy" or too fast. | Tunable `dampingRatio` and `stiffness` in `SpringSpec`. |

## Sources

- [Glance Documentation: Best Practices for Widgets](https://developer.android.com/jetpack/compose/glance)
- [Android 15+ Accessibility Security Updates](https://developer.android.com/about/versions/15)
- [Compose Performance Guidelines](https://developer.android.com/jetpack/compose/performance)
