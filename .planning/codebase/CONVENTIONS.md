# Coding Conventions

**Analysis Date:** 2025-05-22

## Naming Patterns

**Files:**
- Use `UpperCamelCase.kt` for Kotlin source files.
- Resource files use `snake_case.xml` (e.g., `zen_stack_widget_info.xml`).

**Functions:**
- Logic functions: `camelCase` (e.g., `addTask`, `togglePriority`).
- `@Composable` functions: `UpperCamelCase` (e.g., `ZenStackApp`, `TaskCard`).

**Variables:**
- General variables: `camelCase`.
- Private backing properties (StateFlow): `_camelCase` (e.g., `_brainDumpTasks`).

**Types:**
- Classes and Interfaces: `UpperCamelCase`.
- Data classes for models: `UpperCamelCase` (e.g., `Task`).

## Code Style

**Formatting:**
- Standard Android/Kotlin style (likely enforced by Android Studio default).
- Indentation: 4 spaces.

**Linting:**
- Not explicitly configured in project root (no `.editorconfig` or `detekt.yml` found), likely relies on default IDE linting.

## Import Organization

**Order:**
1. Android/AndroidX imports.
2. Kotlin library imports.
3. Project-specific imports.
(Example from `TaskViewModel.kt`)

**Path Aliases:**
- None detected.

## Error Handling

**Patterns:**
- Basic validation in ViewModels (e.g., `if (name.isBlank()) return`).
- Asynchronous operations wrapped in `viewModelScope.launch` for lifecycle safety.

## Logging

**Framework:**
- None detected (no `Log.d` or third-party logger usage observed).

## Comments

**When to Comment:**
- Minimal commenting observed.
- Standard boilerplate comments for tests.

**JSDoc/TSDoc:**
- KDoc not widely used in the existing sample files.

## Function Design

**Size:**
- UI components are broken down into smaller composables (e.g., `BrainDumpStep`, `Power3Step`).

**Parameters:**
- Composables typically take a `Modifier` as the first optional parameter (standard practice, though not always followed in internal steps).
- Callback parameters use the `onEventName: () -> Unit` pattern.

**Return Values:**
- Composable functions return `Unit`.
- Logic functions in ViewModels typically return `Unit` (state-driven).

## Module Design

**Exports:**
- Standard Kotlin visibility (defaults to `public`).

**Barrel Files:**
- Not applicable in this Kotlin project.

---

*Convention analysis: 2025-05-22*
