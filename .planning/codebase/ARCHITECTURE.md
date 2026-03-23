# Architecture

**Analysis Date:** 2026-03-23

## Pattern Overview

**Overall:** MVVM (Model-View-ViewModel) for the Android application, combined with Jetpack Compose for the UI and Room for data persistence.

**Key Characteristics:**
- **Reactive UI:** Built entirely with Jetpack Compose, reacting to state changes in the ViewModel.
- **Unidirectional Data Flow (UDF):** State flows down from the ViewModel to the Compose UI, and events (user actions) flow up from the UI to the ViewModel.
- **Single Source of Truth:** Room database serves as the persistent source of truth for task data.
- **State-Driven Navigation:** The "Setup Flow" is managed by internal state within `ZenStackApp.kt` rather than a formal navigation library.

## Layers

**Data Layer:**
- Purpose: Handles data persistence and retrieval using Room database.
- Location: `app/src/main/java/hr/fipu/organizationtool/data/`
- Contains: Entity (`Task.kt`), DAO (`TaskDao.kt`), and Database configuration (`AppDatabase.kt`).
- Depends on: Room Persistence Library.
- Used by: `TaskViewModel.kt`, `ZenStackWidget.kt`.

**UI Layer (Presentation):**
- Purpose: Displays the UI and handles user interaction.
- Location: `app/src/main/java/hr/fipu/organizationtool/ui/`
- Contains: ViewModel (`TaskViewModel.kt`), Main UI Composables (`ZenStackApp.kt`), and Theme definitions.
- Depends on: Data Layer (via DAO), Jetpack Compose, Android Lifecycle.
- Used by: `MainActivity.kt`.

**Widget Layer:**
- Purpose: Provides an at-a-glance view of tasks on the Android home screen.
- Location: `app/src/main/java/hr/fipu/organizationtool/widget/`
- Contains: Glance Widget (`ZenStackWidget.kt`) and its action handler (`TaskActionHandler.kt`).
- Depends on: Data Layer, Jetpack Glance.
- Used by: Android Home Screen / Launcher.

## Data Flow

**Task Management Flow:**

1. **User Action:** User adds or toggles a task in the UI (`ZenStackApp.kt`).
2. **ViewModel Update:** The `TaskViewModel.kt` receives the event and updates its internal `StateFlow` or calls the `TaskDao`.
3. **Persistence:** `TaskDao.kt` performs the database operation (insert, update, delete).
4. **Reactivity:** Room returns a `Flow<List<Task>>` from `getAllTasks()`, which is collected as state in the UI.
5. **UI Update:** Compose automatically recomposes the UI with the latest data.
6. **Widget Sync:** After certain updates, `ZenStackWidget().updateAll(context)` is called to refresh the home screen widget.

**State Management:**
- ViewModel uses `MutableStateFlow` for temporary setup state (brain dump tasks, selected priority IDs).
- UI uses `collectAsState()` to observe both Room `Flow` and ViewModel `StateFlow`.

## Key Abstractions

**Task Entity:**
- Purpose: Represents a single unit of work with priority and completion status.
- Examples: `app/src/main/java/hr/fipu/organizationtool/data/Task.kt`
- Pattern: Room @Entity.

**TaskViewModel:**
- Purpose: Orchestrates UI state and business logic.
- Examples: `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`
- Pattern: AndroidViewModel.

**ZenStackWidget:**
- Purpose: Home screen representation of the app's core value proposition (Focus).
- Examples: `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`
- Pattern: GlanceAppWidget.

## Entry Points

**MainActivity:**
- Location: `app/src/main/java/hr/fipu/organizationtool/MainActivity.kt`
- Triggers: App Launch.
- Responsibilities: Initializes the Jetpack Compose environment and sets the content to `ZenStackApp`.

**ZenStackWidget:**
- Location: `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`
- Triggers: Periodic updates or manual `updateAll` calls.
- Responsibilities: Renders the widget UI and handles clicks via `TaskActionHandler`.

## Error Handling

**Strategy:** Minimal explicit error handling in current state.

**Patterns:**
- No global error handler detected.
- Room database operations are performed in `viewModelScope` using coroutines.

## Cross-Cutting Concerns

**Logging:** Not explicitly implemented in source files (mostly default Logcat if any).
**Validation:** Basic string validation (e.g., `isNotBlank()`) in `TaskViewModel.addTask`.
**Authentication:** Not applicable (local-only app).

---

*Architecture analysis: 2026-03-23*
