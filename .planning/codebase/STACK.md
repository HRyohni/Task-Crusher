# Technology Stack

**Analysis Date:** 2025-02-14

## Languages

**Primary:**
- Kotlin 2.0.21 - Used for application logic, UI with Jetpack Compose, and Room database configuration. `app/src/main/java/hr/fipu/organizationtool/**/*.kt`

**Secondary:**
- Kotlin DSL - Used for Gradle build configuration. `build.gradle`, `app/build.gradle`.

## Runtime

**Environment:**
- Android SDK (Target 36, Compile 36, Min 28) - Runs on Android devices. `app/build.gradle`

**Package Manager:**
- Gradle 8.13 - Used for dependency management and build automation.
- Lockfile: `gradle/libs.versions.toml` (Version Catalog) is used for version management.

## Frameworks

**Core:**
- Jetpack Compose (BOM 2024.09.00) - Declarative UI framework. `app/build.gradle`
- Android Jetpack - Core Android components (Core KTX, Activity, Navigation, Lifecycle). `app/build.gradle`

**Testing:**
- JUnit 4.13.2 - Unit testing. `gradle/libs.versions.toml`
- Espresso Core 3.7.0 - UI testing. `gradle/libs.versions.toml`
- Compose Test JUnit4 - Testing Compose UI. `app/build.gradle`

**Build/Dev:**
- Android Gradle Plugin (AGP) 8.13.2 - Build tool for Android apps. `gradle/libs.versions.toml`
- KSP (Kotlin Symbol Processing) 2.0.21-1.0.27 - Used for Room annotation processing. `gradle/libs.versions.toml`

## Key Dependencies

**Critical:**
- androidx.room (2.8.4) - Local database abstraction. `app/src/main/java/hr/fipu/organizationtool/data/AppDatabase.kt`
- androidx.glance (1.1.1) - Used for creating app widgets using Compose-like syntax. `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`
- androidx.navigation.compose (2.9.7) - Navigation management within the Compose UI. `app/src/main/java/hr/fipu/organizationtool/ui/ZenStackApp.kt`

**Infrastructure:**
- androidx.lifecycle (2.9.4) - ViewModel and lifecycle management. `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`

## Configuration

**Environment:**
- Managed via `gradle.properties` and `local.properties`.

**Build:**
- Root `build.gradle`
- App-level `app/build.gradle`
- Version Catalog `gradle/libs.versions.toml`

## Platform Requirements

**Development:**
- JDK 11 (specified as source and target compatibility). `app/build.gradle`
- Android Studio or IntelliJ IDEA with Android support.

**Production:**
- Android devices with API level 28 or higher. `app/build.gradle`

---

*Stack analysis: 2025-02-14*
