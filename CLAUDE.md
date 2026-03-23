<!-- GSD:project-start source:PROJECT.md -->
## Project

**ZenStack (Working Title)**

**Core Value:** **Focus through Frictionless Intent:** Minimize the gap between a thought and its capture, then force a choice to prevent paralysis.
<!-- GSD:project-end -->

<!-- GSD:stack-start source:codebase/STACK.md -->
## Technology Stack

## Languages
- Kotlin 2.0.21 - Used for application logic, UI with Jetpack Compose, and Room database configuration. `app/src/main/java/hr/fipu/organizationtool/**/*.kt`
- Kotlin DSL - Used for Gradle build configuration. `build.gradle`, `app/build.gradle`.
## Runtime
- Android SDK (Target 36, Compile 36, Min 28) - Runs on Android devices. `app/build.gradle`
- Gradle 8.13 - Used for dependency management and build automation.
- Lockfile: `gradle/libs.versions.toml` (Version Catalog) is used for version management.
## Frameworks
- Jetpack Compose (BOM 2024.09.00) - Declarative UI framework. `app/build.gradle`
- Android Jetpack - Core Android components (Core KTX, Activity, Navigation, Lifecycle). `app/build.gradle`
- JUnit 4.13.2 - Unit testing. `gradle/libs.versions.toml`
- Espresso Core 3.7.0 - UI testing. `gradle/libs.versions.toml`
- Compose Test JUnit4 - Testing Compose UI. `app/build.gradle`
- Android Gradle Plugin (AGP) 8.13.2 - Build tool for Android apps. `gradle/libs.versions.toml`
- KSP (Kotlin Symbol Processing) 2.0.21-1.0.27 - Used for Room annotation processing. `gradle/libs.versions.toml`
## Key Dependencies
- androidx.room (2.8.4) - Local database abstraction. `app/src/main/java/hr/fipu/organizationtool/data/AppDatabase.kt`
- androidx.glance (1.1.1) - Used for creating app widgets using Compose-like syntax. `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`
- androidx.navigation.compose (2.9.7) - Navigation management within the Compose UI. `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt`
- androidx.lifecycle (2.9.4) - ViewModel and lifecycle management. `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`
## Configuration
- Managed via `gradle.properties` and `local.properties`.
- Root `build.gradle`
- App-level `app/build.gradle`
- Version Catalog `gradle/libs.versions.toml`
## Platform Requirements
- JDK 11 (specified as source and target compatibility). `app/build.gradle`
- Android Studio or IntelliJ IDEA with Android support.
- Android devices with API level 28 or higher. `app/build.gradle`
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## Conventions

## Naming Patterns
- Use `UpperCamelCase.kt` for Kotlin source files.
- Resource files use `snake_case.xml` (e.g., `zen_stack_widget_info.xml`).
- Logic functions: `camelCase` (e.g., `addTask`, `togglePriority`).
- `@Composable` functions: `UpperCamelCase` (e.g., `ZenStackApp`, `TaskCard`).
- General variables: `camelCase`.
- Private backing properties (StateFlow): `_camelCase` (e.g., `_brainDumpTasks`).
- Classes and Interfaces: `UpperCamelCase`.
- Data classes for models: `UpperCamelCase` (e.g., `Task`).
## Code Style
- Standard Android/Kotlin style (likely enforced by Android Studio default).
- Indentation: 4 spaces.
- Not explicitly configured in project root (no `.editorconfig` or `detekt.yml` found), likely relies on default IDE linting.
## Import Organization
- None detected.
## Error Handling
- Basic validation in ViewModels (e.g., `if (name.isBlank()) return`).
- Asynchronous operations wrapped in `viewModelScope.launch` for lifecycle safety.
## Logging
- None detected (no `Log.d` or third-party logger usage observed).
## Comments
- Minimal commenting observed.
- Standard boilerplate comments for tests.
- KDoc not widely used in the existing sample files.
## Function Design
- UI components are broken down into smaller composables (e.g., `BrainDumpStep`, `Power3Step`).
- Composables typically take a `Modifier` as the first optional parameter (standard practice, though not always followed in internal steps).
- Callback parameters use the `onEventName: () -> Unit` pattern.
- Composable functions return `Unit`.
- Logic functions in ViewModels typically return `Unit` (state-driven).
## Module Design
- Standard Kotlin visibility (defaults to `public`).
- Not applicable in this Kotlin project.
<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## Architecture

## Pattern Overview
- **Reactive UI:** Built entirely with Jetpack Compose, reacting to state changes in the ViewModel.
- **Unidirectional Data Flow (UDF):** State flows down from the ViewModel to the Compose UI, and events (user actions) flow up from the UI to the ViewModel.
- **Single Source of Truth:** Room database serves as the persistent source of truth for task data.
- **State-Driven Navigation:** The "Setup Flow" is managed by internal state within `ZenStackApp.kt` rather than a formal navigation library.
## Layers
- Purpose: Handles data persistence and retrieval using Room database.
- Location: `app/src/main/java/hr/fipu/organizationtool/data/`
- Contains: Entity (`Task.kt`), DAO (`TaskDao.kt`), and Database configuration (`AppDatabase.kt`).
- Depends on: Room Persistence Library.
- Used by: `TaskViewModel.kt`, `ZenStackWidget.kt`.
- Purpose: Displays the UI and handles user interaction.
- Location: `app/src/main/java/hr/fipu/organizationtool/ui/`
- Contains: ViewModel (`TaskViewModel.kt`), Main UI Composables (`ZenStackApp.kt`), and Theme definitions.
- Depends on: Data Layer (via DAO), Jetpack Compose, Android Lifecycle.
- Used by: `MainActivity.kt`.
- Purpose: Provides an at-a-glance view of tasks on the Android home screen.
- Location: `app/src/main/java/hr/fipu/organizationtool/widget/`
- Contains: Glance Widget (`ZenStackWidget.kt`) and its action handler (`TaskActionHandler.kt`).
- Depends on: Data Layer, Jetpack Glance.
- Used by: Android Home Screen / Launcher.
## Data Flow
- ViewModel uses `MutableStateFlow` for temporary setup state (brain dump tasks, selected priority IDs).
- UI uses `collectAsState()` to observe both Room `Flow` and ViewModel `StateFlow`.
## Key Abstractions
- Purpose: Represents a single unit of work with priority and completion status.
- Examples: `app/src/main/java/hr/fipu/organizationtool/data/Task.kt`
- Pattern: Room @Entity.
- Purpose: Orchestrates UI state and business logic.
- Examples: `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`
- Pattern: AndroidViewModel.
- Purpose: Home screen representation of the app's core value proposition (Focus).
- Examples: `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`
- Pattern: GlanceAppWidget.
## Entry Points
- Location: `app/src/main/java/hr/fipu/organizationtool/MainActivity.kt`
- Triggers: App Launch.
- Responsibilities: Initializes the Jetpack Compose environment and sets the content to `ZenStackApp`.
- Location: `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`
- Triggers: Periodic updates or manual `updateAll` calls.
- Responsibilities: Renders the widget UI and handles clicks via `TaskActionHandler`.
## Error Handling
- No global error handler detected.
- Room database operations are performed in `viewModelScope` using coroutines.
## Cross-Cutting Concerns
<!-- GSD:architecture-end -->

<!-- GSD:workflow-start source:GSD defaults -->
## GSD Workflow Enforcement

Before using Edit, Write, or other file-changing tools, start work through a GSD command so planning artifacts and execution context stay in sync.

Use these entry points:
- `/gsd:quick` for small fixes, doc updates, and ad-hoc tasks
- `/gsd:debug` for investigation and bug fixing
- `/gsd:execute-phase` for planned phase work

Do not make direct repo edits outside a GSD workflow unless the user explicitly asks to bypass it.
<!-- GSD:workflow-end -->



<!-- GSD:profile-start -->
## Developer Profile

> Profile not yet configured. Run `/gsd:profile-user` to generate your developer profile.
> This section is managed by `generate-claude-profile` -- do not edit manually.
<!-- GSD:profile-end -->
