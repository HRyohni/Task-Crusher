# Architecture Patterns: ZenStack

**Domain:** Minimalist Productivity
**Researched:** May 2025
**Overall Confidence:** MEDIUM

## Recommended Architecture

ZenStack follows a **UDF (Unidirectional Data Flow)** pattern but adopts the "2025 Direct State" optimization where possible to minimize boilerplate.

### Component Boundaries

| Component | Responsibility | Communicates With |
|-----------|---------------|-------------------|
| **UI (Compose)** | Rendering views, handling tactile animations, observing State. | ViewModels |
| **ViewModel** | Managing UI state via `mutableStateOf`, handling user intent. | Use Cases / Repository |
| **Widget (Glance)** | Rendering home screen widget, handling direct actions. | TaskDao / Repository |
| **Repository** | Data orchestration (Local DB vs In-memory). | Room DAO, DataStore |
| **Accessibility Service**| Detecting Back Tap / Gestures (Optional). | Application Context / Intent |

### Data Flow

1. User interacts with "Tactile Bubble" → UI calls `viewModel.toggleSelection(id)`.
2. ViewModel updates `mutableStateOf` list → UI recomposes instantly.
3. User finishes session → ViewModel saves to `TaskRepository`.
4. `TaskRepository` updates Room DB.
5. `GlanceAppWidgetManager` triggers a widget refresh.

## Patterns to Follow

### Pattern 1: Direct ViewModel State (2025 Standard)
**What:** Using `androidx.compose.runtime.State` directly in ViewModels instead of `StateFlow`.
**When:** For UI-only state that doesn't need to be observed by non-Compose consumers.
**Example:**
```kotlin
class TaskViewModel : ViewModel() {
    // Direct state allows for cleaner access in UI without .collectAsState()
    var tasks by mutableStateOf<List<Task>>(emptyList())
        private set

    fun loadTasks() { /* ... */ }
}
```

### Pattern 2: Anchored Physics (Tactile UI)
**What:** Using `AnchoredDraggableState` for physically-bound interactions.
**When:** The "Brain Dump" bubbles and "The Power 3" selection.
**Rationale:** Creates a sense of weight and friction, making the app feel "real."

## Anti-Patterns to Avoid

### Anti-Pattern 1: Heavy Widget Logic
**What:** Performing complex sorting or filtering inside the `GlanceAppWidget.provideContent`.
**Why bad:** Causes widget lag and battery drain.
**Instead:** Prepare a "Widget-Ready" data model in the Repository and save it to DataStore or a specific Table for the widget to read directly.

## Scalability Considerations

| Concern | At 100 users | At 10K users | At 1M users |
|---------|--------------|--------------|-------------|
| **Data Growth** | Room handles fine. | Occasional DB vacuuming. | Local-only; user device is the limit. |
| **Widget Sync** | Instant. | Respect system limits (refresh intervals). | Batch updates to avoid system throttling. |

## Sources

- [Modern Android App Architecture Guide (2025)](https://developer.android.com/topic/architecture)
- [Compose State in ViewModel Discussion](https://medium.com/androiddevelopers)
