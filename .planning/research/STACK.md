# Technology Stack: ZenStack

**Project:** ZenStack (Minimalist Productivity)
**Researched:** May 2025
**Overall Confidence:** HIGH

## Recommended Stack (2025 Standard)

### Core Framework
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Kotlin** | 2.1.0 | Language | K2 compiler stability and improved performance for Compose. |
| **Jetpack Compose** | BOM 2025.05.00 | UI Toolkit | Support for `LookaheadScope`, `animateBounds`, and 2D scroll APIs. |
| **Material 3 Expressive** | 1.4.0 | Design System | "Emotional UX" components, spring-based motion physics, and morphing indicators. |
| **Jetpack Glance** | 1.2.0 | Home Widgets | Stable `providePreview` and `setWidgetPreview` for high-quality widget picker experiences. |

### Database & State
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Room (KMP)** | 2.7.0 | Persistence | Local-only focus with KMP-ready architecture for future portability. |
| **DataStore** | 1.1.0 | Settings | Preference storage for session states and user guides. |
| **ViewModel** | 2.9.0 | State Holder | Direct use of Compose `State` in VM (2025 pattern) for cleaner reactivity. |

### Infrastructure & Patterns
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Koin** | 4.0.0 | Dependency Inj. | Lightweight, Kotlin-first DSL that fits the "Zen" minimalist philosophy better than Hilt. |
| **Navigation 3** | 1.0.0-alpha | Navigation | Superior shared element transitions and modular navigation control. |
| **Media3** | 1.5.0 | Audio Feedback | Standard for high-performance low-latency "Success" sounds. |

### Tactile & Sensory Libraries
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| **androidx.compose.foundation** | 1.8.0+ | Tactile UI | `AnchoredDraggable` for bubble interactions; `Modifier.onPlaced` for spatial logic. |
| **Native Vibrator** | API 31+ | Haptics | `VibrationEffect.startComposition()` for "crisp" vs "thud" sensations. |

## Alternatives Considered

| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| DI | Koin | Hilt | Hilt adds boilerplate; Koin is more "Zen" and faster for small-medium apps. |
| Data | Room | SQLDelight | Room is more familiar and has excellent Compose integration in 2025. |
| Navigation | Navigation 3 | Simple Backstack | Nav 3 handles shared elements (tactile feel) with significantly less code. |

## Installation

```kotlin
// libs.versions.toml
[versions]
kotlin = "2.1.0"
composeBom = "2025.05.00"
material3 = "1.4.0"
glance = "1.2.0"
koin = "4.0.0"
room = "2.7.0"
media3 = "1.5.0"

[libraries]
# Core
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "material3" }
androidx-glance-appwidget = { group = "androidx.glance", name = "glance-appwidget", version.ref = "glance" }

# Tactile
androidx-compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }
androidx-media3-exoplayer = { group = "androidx.media3", name = "media3-exoplayer", version.ref = "media3" }

# Data & DI
koin-android = { group = "io.insert-koin", name = "koin-android", version.ref = "koin" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
```

## Sources

- [Android Developers Blog: Material 3 Expressive](https://android-developers.googleblog.com/)
- [Jetpack Glance Release Notes](https://developer.android.com/jetpack/androidx/releases/glance)
- [Compose Animation Guide 2025](https://developer.android.com/jetpack/compose/animation)
- [Koin 4.0 Release Announcement](https://insert-koin.io/)
