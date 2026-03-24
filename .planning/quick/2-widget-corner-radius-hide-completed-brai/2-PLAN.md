---
quick: 2
type: execute
wave: 1
depends_on: []
files_modified:
  - app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt
  - app/src/main/java/hr/fipu/organizationtool/ui/CalendarScreen.kt
autonomous: true

must_haves:
  truths:
    - "Widget displays with rounded corners (16dp radius)"
    - "Widget brain dump section hides tasks with status COMPLETED"
    - "Calendar screen shows full 90-day heatmap grid without truncation"
    - "Calendar task detail list scrolls without nested scroll conflict"
  artifacts:
    - path: "app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt"
      provides: "cornerRadius modifier on LazyColumn, filtered activeBrainDumpTasks"
    - path: "app/src/main/java/hr/fipu/organizationtool/ui/CalendarScreen.kt"
      provides: "vertically scrollable outer Column, wrapContentHeight grid, Column forEach task list"
  key_links:
    - from: "ZenStackWidget.kt"
      to: "androidx.glance.appwidget.cornerRadius"
      via: "import + modifier chain"
      pattern: "cornerRadius\\(16\\.dp\\)"
    - from: "CalendarScreen.kt"
      to: "outer Column"
      via: "verticalScroll(rememberScrollState())"
      pattern: "verticalScroll"
---

<objective>
Fix three cosmetic/layout issues: add rounded corners to the widget container, hide completed tasks from the widget brain dump section, and fix the calendar screen overflow that cuts off the heatmap grid and causes nested scroll conflicts.

Purpose: The widget looks visually unpolished without corner radius, completed brain dump tasks clutter the widget unnecessarily, and the calendar screen is broken on shorter displays.
Output: Updated ZenStackWidget.kt and CalendarScreen.kt.
</objective>

<execution_context>
@C:/Users/Kulen/.claude/get-shit-done/workflows/execute-plan.md
@C:/Users/Kulen/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/STATE.md
@.planning/PROJECT.md
</context>

<tasks>

<task type="auto">
  <name>Task 1: Widget — corner radius and hide completed brain dump tasks</name>
  <files>app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt</files>
  <action>
    1. Add import at the top of the file:
       `import androidx.glance.appwidget.cornerRadius`

    2. On the LazyColumn modifier chain (line 43), append `.cornerRadius(16.dp)` after `.padding(12.dp)`:
       ```
       LazyColumn(modifier = GlanceModifier.fillMaxSize().background(ColorProvider(ZenSurface)).padding(12.dp).cornerRadius(16.dp))
       ```

    3. Inside `provideContent`, after the `otherTasks` delegation (line 39), derive a filtered list:
       ```kotlin
       val activeBrainDumpTasks = otherTasks.filter { it.status != "COMPLETED" }
       ```

    4. Replace both usages of `otherTasks` in the brain dump section with `activeBrainDumpTasks`:
       - The guard condition: `if (activeBrainDumpTasks.isNotEmpty())`
       - The items call: `items(activeBrainDumpTasks) { task -> TaskItem(task, isSmall = true) }`
  </action>
  <verify>Project builds without errors: `./gradlew assembleDebug` from repo root.</verify>
  <done>Build succeeds. Widget renders with rounded corners. Brain dump section is absent when all non-priority tasks are completed, and omits individual completed tasks when only some are done.</done>
</task>

<task type="auto">
  <name>Task 2: CalendarScreen — fix overflow, remove nested scroll conflict</name>
  <files>app/src/main/java/hr/fipu/organizationtool/ui/CalendarScreen.kt</files>
  <action>
    1. Add two imports after the existing foundation imports:
       ```kotlin
       import androidx.compose.foundation.rememberScrollState
       import androidx.compose.foundation.verticalScroll
       ```

    2. Outer Column modifier (line 48): change `modifier.fillMaxSize()` to
       `modifier.fillMaxWidth().verticalScroll(rememberScrollState())`

    3. LazyVerticalGrid modifier (lines 82-87): remove `.heightIn(max = 300.dp)`, replace with `.wrapContentHeight()`:
       ```kotlin
       modifier = Modifier
           .fillMaxWidth()
           .padding(horizontal = 4.dp)
           .wrapContentHeight()
       ```

    4. Replace the `Box(...weight(1f))` wrapping the day detail panel (lines 120-163) with a plain `Box` using only `fillMaxWidth()`:
       ```kotlin
       Box(
           modifier = Modifier.fillMaxWidth(),
           contentAlignment = Alignment.Center
       )
       ```
       Remove `.weight(1f)` — it has no effect inside a vertically scrollable Column and causes a runtime exception.

    5. Replace the inner `LazyColumn` (lines 142-160) with a plain `Column` + `forEach` since the parent is now scrollable (nested lazy scrollables in the same axis are not supported):
       ```kotlin
       Column {
           tasksForSelectedDay.forEach { task ->
               Surface(
                   shape = RoundedCornerShape(8.dp),
                   color = MaterialTheme.colorScheme.surfaceVariant,
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(horizontal = 16.dp, vertical = 4.dp)
               ) {
                   Text(
                       text = task.title,
                       modifier = Modifier.padding(12.dp),
                       style = MaterialTheme.typography.bodyLarge
                   )
               }
           }
       }
       ```

    6. Remove the now-unused `LazyColumn` import from `androidx.compose.foundation.lazy.LazyColumn` and the `items` import from `androidx.compose.foundation.lazy.items` only if they are no longer referenced elsewhere in the file. Check before removing — `androidx.compose.foundation.lazy.grid.items` (the grid variant) must stay.
  </action>
  <verify>Project builds without errors: `./gradlew assembleDebug`</verify>
  <done>Build succeeds. Calendar screen shows full 13-week grid without truncation. Scrolling through the full screen works without crash or nested scroll warning.</done>
</task>

</tasks>

<verification>
Run `./gradlew assembleDebug` — zero compile errors.

Manual checks on device/emulator:
- Widget: rounded corners visible on home screen; completed brain dump tasks do not appear.
- Calendar: full heatmap grid visible; tapping a day with tasks shows the task list inline; no crash on scroll.
</verification>

<success_criteria>
- `./gradlew assembleDebug` passes with no errors.
- Widget LazyColumn has `.cornerRadius(16.dp)` in modifier chain.
- `activeBrainDumpTasks` filters out `status == "COMPLETED"` tasks before rendering.
- CalendarScreen outer Column uses `verticalScroll(rememberScrollState())` with `fillMaxWidth()`.
- LazyVerticalGrid uses `wrapContentHeight()` (no `heightIn` cap).
- Task detail list is a plain `Column` with `forEach` (no nested LazyColumn).
- `Box` wrapping the detail panel has no `.weight(1f)`.
</success_criteria>

<output>
After completion, create `.planning/quick/2-widget-corner-radius-hide-completed-brai/2-SUMMARY.md` using the summary template.
</output>
