# Phase 1: Foundation & Data Architecture - Research

**Researched:** March 23, 2026
**Domain:** Kotlin 2.1, Material 3 Expressive, Room KMP, Glance 1.2
**Confidence:** HIGH

<user_constraints>
## User Constraints (from 01-CONTEXT.md)

### Locked Decisions
- **Zen Palette (Fixed)** — Use a curated grayscale palette with a single soft accent color.
- **Physical Shadows** — Utilize subtle physical shadows to emphasize the "tactile" nature of bubbles and tags.
- **Modern Sans-Serif** — Use a clean, geometric sans-serif (e.g., Inter or Roboto).
- **Global Motion Physics** — Define standard Damping and Stiffness constants within the global theme.

### Claude's Discretion
- **Dependency Management:** Choice between Koin 4.0 or manual DI (Koin 4.0 recommended).
- **Data Migration:** Decision between clean migration or fresh schema.
- **Testing Setup:** Setup of KMP-native testing architecture for the Repository.

### Deferred Ideas (OUT OF SCOPE)
- **Tactile Physics Tuning:** Actual physics-based "bubbles" are deferred to Phase 5.
- **Glance Interactivity:** Full widget interactive loops are deferred to Phase 3.
</user_constraints>

<research_summary>
## Summary

This research covers the foundational setup for ZenStack using the 2025/2026 Android stack. The primary focus is the upgrade to **Kotlin 2.1 (K2)** and **Material 3 Expressive (1.4.0)**, along with a **Room KMP** implementation that supports cross-process synchronization for **Glance 1.2.0** widgets.

Key findings include the new Gradle plugin-based Compose compiler configuration, the introduction of `MotionScheme` in M3 Expressive for physics-based animations, and the critical requirement of `enableMultiInstanceInvalidation()` for Room to sync data between the main app and the Glance widget process.

**Primary recommendation:** Use the `org.jetbrains.kotlin.plugin.compose` plugin with Kotlin 2.1.0, implement `MotionScheme.expressive()` for the "Zen" tactile feel, and ensure Room KMP uses `BundledSQLiteDriver` with multi-instance invalidation enabled in the Android-specific builder.
</research_summary>

<standard_stack>
## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Kotlin | 2.1.0 | Language | K2 compiler performance and Compose stability. |
| Material 3 | 1.4.0 | UI Components | "Expressive" motion and emotional UX components. |
| Room (KMP) | 2.7.0 | Persistence | Unified data layer with cross-process support. |
| Jetpack Glance | 1.2.0 | Home Widgets | Stable interactive widget support. |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Koin | 4.0.0 | DI | Lightweight KMP-ready dependency injection. |
| KSP | 2.1.0-1.0.29| Code Gen | Required for Room and Koin. |
| Media3 | 1.5.0 | Audio | Low-latency feedback for Phase 6. |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Koin 4.0 | Manual DI | Manual is "Zen" but adds boilerplate as the app grows. |
| Room KMP | SQLDelight | SQLDelight is powerful but Room has better Compose/Flow integration. |

**Installation:**
```kotlin
// libs.versions.toml
[versions]
kotlin = "2.1.0"
room = "2.7.0"
material3 = "1.4.0"
glance = "1.2.0"
koin = "4.0.0"

[plugins]
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
androidx-room = { id = "androidx.room", version.ref = "room" }
```
</standard_stack>

<architecture_patterns>
## Architecture Patterns

### Recommended Project Structure (KMP-Ready)
```
app/src/
├── commonMain/kotlin/        # Shared Data/Domain (Room, Repos)
├── androidMain/kotlin/       # Android-specific (DB Builders, Glance)
└── main/                     # Android App UI & Manifest
```

### Pattern 1: Room Multi-Instance Invalidation
**What:** Enabling sync between app and widget processes.
**When to use:** Any app with a Glance widget or separate service process.
**Example:**
```kotlin
// androidMain
actual fun RoomDatabase.Builder<AppDatabase>.configurePlatform(): RoomDatabase.Builder<AppDatabase> {
    return this.enableMultiInstanceInvalidation()
}
```

### Pattern 2: Material 3 Expressive Motion
**What:** Global application of spring-based motion.
**When to use:** Application theme initialization.
**Example:**
```kotlin
@Composable
fun ZenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = zenColorScheme,
        motionScheme = MotionScheme.expressive(), // Bouncy, "heroic" feel
        content = content
    )
}
```

### Anti-Patterns to Avoid
- **Legacy Duration Tweaking:** Avoid `tween()` for tactile interactions; prefer `spring()` or the built-in `motionScheme` tokens.
- **Static Widget Previews:** Don't skip `providePreview` in Glance 1.2; it's critical for the modern Android widget picker.
</architecture_patterns>

<dont_hand_roll>
## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Cross-process Sync | Custom ContentProvider | Room MII | `enableMultiInstanceInvalidation()` handles SQLite file locks and invalidation. |
| Spring Physics | Custom Math | M3 MotionScheme | Research-backed "expressive" physics are built-in. |
| Widget Flow | Manual Refresh Intents | Glance 1.2.0 Flow | `collectAsState` in `provideContent` reacts to Room changes automatically. |

**Key insight:** Room's Multi-Instance Invalidation is the only reliable way to handle the 150ms-500ms latency requirement for widget updates without manual, error-prone BroadcastIntents.
</dont_hand_roll>

<common_pitfalls>
## Common Pitfalls

### Pitfall 1: Missing Icon Dependencies
**What goes wrong:** `Icon` components fail to compile or show generic boxes.
**Why it happens:** Material 3 1.4.0 removed implicit `material-icons-core` dependency.
**How to avoid:** Explicitly add `androidx.compose.material:material-icons-core` and `extended`.

### Pitfall 2: Compose Compiler Mismatch
**What goes wrong:** Build failure: "Compose Compiler requires Kotlin X.X.X".
**Why it happens:** The new `org.jetbrains.kotlin.plugin.compose` must match the Kotlin version exactly.
**How to avoid:** Use `alias(libs.plugins.compose.compiler)` with the same version ref as Kotlin.

### Pitfall 3: SQLite File Locking
**What goes wrong:** `SQLiteDatabaseLockedException` when app and widget write simultaneously.
**Why it happens:** Accessing the same file from different processes without Write-Ahead Logging (WAL).
**How to avoid:** Room enables WAL by default on Android, but ensure `enableMultiInstanceInvalidation()` is called.
</common_pitfalls>

<code_examples>
## Code Examples

### Room KMP Shared Database
```kotlin
// commonMain
@Database(entities = [Task::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
```

### Custom Expressive Spring Constants
```kotlin
// Source: Project Decisions
val ZenStiffness = Spring.StiffnessMediumLow
val ZenDamping = 0.75f // "Soft accent" bounce

@Composable
fun ZenSpringSpec<T>() = spring<T>(
    stiffness = ZenStiffness,
    dampingRatio = ZenDamping
)
```

### Glance 1.2.0 Room Observation
```kotlin
class ZenWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = KoinJavaComponent.get<TaskRepository>(TaskRepository::class.java)
        
        provideContent {
            val tasks by repository.observeTopTasks().collectAsState(emptyList())
            ZenWidgetContent(tasks)
        }
    }
}
```
</code_examples>

<sota_updates>
## State of the Art (2025-2026)

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| `kotlinCompilerExtensionVersion` | `compose-compiler` plugin | Kotlin 2.0+ | Decouples Compose from specific Kotlin patches. |
| Duration-based UI | Physics-based UI | M3 1.4.0 | Interactions feel "tactile" and "alive." |
| Android-only Room | Room KMP | Room 2.7.0 | Data layer is portable to iOS/Desktop. |

**Deprecated/outdated:**
- `material3:1.3.x`: Replaced by 1.4.0 for Expressive features.
- `glance:1.1.x`: Upgrade to 1.2.0 for improved preview APIs.
</sota_updates>

<open_questions>
## Open Questions

1. **Exact Damping Ratio for "Zen" feel?**
   - What we know: Expressive defaults to ~0.7f.
   - What's unclear: If 0.75f provides the intended "calm but active" feel.
   - Recommendation: Experiment in Phase 1 with a demo screen.

2. **Koin 4.0 KMP Stability?**
   - What we know: Beta was stable in late 2024.
   - What's unclear: Final production quirks in Kotlin 2.1.
   - Recommendation: Fallback to manual DI if Koin annotations fail under K2.
</open_questions>

<sources>
## Sources

### Primary (HIGH confidence)
- `androidx.compose.material3:1.4.0` Release Notes - `MotionScheme` and SplitButton APIs.
- `androidx.room:2.7.0` KMP Guide - Multi-platform setup and `enableMultiInstanceInvalidation`.
- `kotlin-lang.org` - Kotlin 2.1.0 release details for Compose.

### Secondary (MEDIUM confidence)
- Android Developers Blog: "Material 3 Expressive" - Design philosophy verification.
- GitHub: `glance-experimental-samples` - Verified Glance 1.2.0 patterns.
</sources>

<metadata>
## Metadata

**Research scope:**
- Core technology: Kotlin 2.1, Material 3 1.4.0
- Ecosystem: Room KMP, Glance 1.2.0, Koin 4.0
- Patterns: Cross-process Room sync, Expressive motion
- Pitfalls: Icon dependencies, compiler mismatch

**Confidence breakdown:**
- Standard stack: HIGH - Current 2026 industry standards.
- Architecture: HIGH - Room KMP is the recommended 2025+ pattern.
- Pitfalls: HIGH - Common migration issues documented.

**Research date:** March 23, 2026
**Valid until:** April 23, 2026
</metadata>

---

*Phase: 01-foundation-data-architecture*
*Research completed: March 23, 2026*
*Ready for planning: yes*
