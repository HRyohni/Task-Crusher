# Codebase Structure

**Analysis Date:** 2026-03-23

## Directory Layout

```
organizationTool/
├── app/                        # Main Android module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/hr/fipu/organizationtool/
│   │   │   │   ├── data/       # Room database and data models
│   │   │   │   ├── ui/         # UI screens, ViewModels, and themes
│   │   │   │   └── widget/     # Home screen widget implementation
│   │   │   └── res/            # Android resources (XML layout, icons, values)
│   │   ├── test/               # Local unit tests
│   │   └── androidTest/        # Instrumented (on-device) tests
│   └── build.gradle            # Module-level Gradle configuration
├── gradle/                     # Gradle wrapper and version catalog
├── build.gradle                # Project-level Gradle configuration
├── settings.gradle             # Project structure and module definitions
└── .planning/                  # GSD planning and documentation
```

## Directory Purposes

**`app/src/main/java/hr/fipu/organizationtool/data/`:**
- Purpose: Persistence layer for the application.
- Contains: Room entities, DAOs, and the database class.
- Key files: `AppDatabase.kt`, `Task.kt`, `TaskDao.kt`.

**`app/src/main/java/hr/fipu/organizationtool/ui/`:**
- Purpose: Presentation layer using Jetpack Compose and MVVM.
- Contains: Composable screens, the primary ViewModel, and design system themes.
- Key files: `ZenStackApp.kt`, `TaskViewModel.kt`, `theme/`.

**`app/src/main/java/hr/fipu/organizationtool/widget/`:**
- Purpose: Home screen integration for rapid access and focus.
- Contains: Glance Widget definitions and action callbacks.
- Key files: `ZenStackWidget.kt`, `TaskActionHandler.kt`.

**`app/src/main/res/`:**
- Purpose: Static assets and XML definitions for the app.
- Contains: Layouts (minimal due to Compose), strings, colors, and adaptive icons.

## Key File Locations

**Entry Points:**
- `app/src/main/java/hr/fipu/organizationtool/MainActivity.kt`: Main UI entry point.
- `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`: Widget entry point.

**Configuration:**
- `app/build.gradle`: App dependencies and build settings.
- `gradle/libs.versions.toml`: Version catalog for centralizing dependency versions.

**Core Logic:**
- `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`: Business logic and state management.
- `app/src/main/java/hr/fipu/organizationtool/data/TaskDao.kt`: Data access operations.

**Testing:**
- `app/src/test/`: Location for JUnit unit tests.
- `app/src/androidTest/`: Location for Compose UI tests and Espresso tests.

## Naming Conventions

**Files:**
- PascalCase for Kotlin classes and files (`ZenStackApp.kt`, `TaskViewModel.kt`).
- snake_case for XML resources (`activity_main.xml`, `ic_launcher.xml`).

**Directories:**
- lower_case for Java packages (`data`, `ui`, `widget`).
- lower_case for resource directories (`drawable`, `layout`, `values`).

## Where to Add New Code

**New Feature:**
- Logic: `app/src/main/java/hr/fipu/organizationtool/ui/` (likely adding to a ViewModel or creating a new one).
- UI: `app/src/main/java/hr/fipu/organizationtool/ui/` (new Composable functions).

**New Data Entity:**
- Implementation: `app/src/main/java/hr/fipu/organizationtool/data/` (Add @Entity, @Dao, and update `AppDatabase`).

**New Component/Module:**
- Shared UI: `app/src/main/java/hr/fipu/organizationtool/ui/components/` (suggested if ui/ grows too large).

**Utilities:**
- Shared helpers: `app/src/main/java/hr/fipu/organizationtool/util/` (not yet present, but recommended for extension functions).

## Special Directories

**`.gradle/`:**
- Purpose: Gradle build tool cache.
- Generated: Yes
- Committed: No

**`app/build/`:**
- Purpose: Build artifacts and intermediate files.
- Generated: Yes
- Committed: No

---

*Structure analysis: 2026-03-23*
