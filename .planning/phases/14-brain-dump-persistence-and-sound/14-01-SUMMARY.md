---
phase: 14-brain-dump-persistence-and-sound
plan: "01"
subsystem: ui/viewmodel
tags: [persistence, shared-preferences, brain-dump, draft]
dependency_graph:
  requires: []
  provides: [brain-dump-draft-persistence]
  affects: [TaskViewModel, setup-flow]
tech_stack:
  added: []
  patterns: [SharedPreferences-lazy-property, JSONArray-title-serialization]
key_files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt
decisions:
  - "org.json.JSONArray used for title serialization — part of Android SDK, no new Gradle dependency"
  - "Titles serialized (not temp IDs) because negative IDs are ephemeral; loadDraftTasks assigns fresh temp IDs on restore"
  - "Draft cleared inside saveSession coroutine after Room inserts complete, so draft only removed when session is safely persisted"
metrics:
  duration_minutes: 1
  completed_date: "2026-03-25"
  tasks_completed: 2
  files_modified: 1
---

# Phase 14 Plan 01: Brain Dump Draft Persistence Summary

SharedPreferences-backed persistence for brain dump task titles using JSONArray serialization and lazy draftPrefs property.

## What Was Built

Added draft persistence to `TaskViewModel` so brain dump tasks typed during setup survive force-stop and reopen:

- `draftPrefs` lazy property backed by `zen_draft` SharedPreferences file
- `saveDraftTasks()` serializes current `_brainDumpTasks` titles to a JSONArray string
- `loadDraftTasks()` restores tasks with fresh negative temp IDs from the stored JSONArray
- `saveDraftTasks()` called at end of `addTask`, `addSuggestedTask`, and `removeTask`
- `loadDraftTasks()` called as first statement in `init {}` (before `loadCompletionHistory()`)
- `draftPrefs.edit().remove("draft_tasks").apply()` called inside `saveSession` coroutine after all Room inserts, clearing the draft only once the session is safely saved

## Deviations from Plan

None - plan executed exactly as written.

## Self-Check: PASSED

- `draftPrefs`, `saveDraftTasks`, `loadDraftTasks` present in TaskViewModel.kt
- `saveDraftTasks()` wired at lines 240, 246, 253 (addTask, addSuggestedTask, removeTask)
- `loadDraftTasks()` at line 308 (first in init)
- `remove("draft_tasks")` at line 332 (inside saveSession coroutine)
- Commit b475205 present
- `./gradlew :app:compileDebugKotlin` exits 0 (BUILD SUCCESSFUL)
