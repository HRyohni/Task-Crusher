# CONCERNS.md

This document identifies technical debt, security risks, performance bottlenecks, and any other known issues or areas of concern in the current codebase.

## Findings Summary

### Tech Debt
1. **In-memory ID Counter:** `TaskViewModel` uses a private `nextId` counter that resets on app restart, while tasks are persisted in Room. This will cause ID collisions.
   - File: `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`
2. **Inefficient Database Retrieval:** `ToggleTaskCallback` in the widget action handler fetches all tasks from the DB and filters them in memory.
   - File: `app/src/main/java/hr/fipu/organizationtool/widget/TaskActionHandler.kt`
3. **Destructive Session Saving:** `saveSession` deletes all tasks before inserting new ones, preventing task history retention.
   - File: `app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt`

### Security Considerations
1. **Backup Rules:** `data_extraction_rules.xml` contains a TODO and is not configured to protect or include specific app data.
   - File: `app/src/main/res/xml/data_extraction_rules.xml`

### Performance Bottlenecks
1. **Widget Data Over-fetching:** The widget loads the entire task list to display only the top 3 priorities.
   - File: `app/src/main/java/hr/fipu/organizationtool/widget/ZenStackWidget.kt`

### Test Coverage Gaps
1. **Missing Tests:** Only boilerplate example tests exist. No unit or integration tests for core logic or persistence.
   - Files: `app/src/test/java/hr/fipu/organizationtool/ExampleUnitTest.kt`, `app/src/androidTest/java/hr/fipu/organizationtool/ExampleInstrumentedTest.kt`
