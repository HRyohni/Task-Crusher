---
phase: 11-achievements-tab
plan: "02"
subsystem: viewmodel
tags: [achievements, streak, speed-run, stateflow, koin]
dependency_graph:
  requires: [AchievementEntity, AchievementDao, AppDatabase-v5, Koin-achievementDao]
  provides: [Achievement-domain-model, TaskViewModel-achievement-logic, newlyUnlockedAchievement-StateFlow, achievements-StateFlow]
  affects: [TaskViewModel, KoinModule]
tech_stack:
  added: []
  patterns: [MutableStateFlow, viewModelScope.launch, suspend-fun, companion-object-definitions]
key_files:
  created: []
  modified:
    - app/src/main/java/hr/fipu/organizationtool/ui/TaskViewModel.kt
    - app/src/main/java/hr/fipu/organizationtool/di/KoinModule.kt
decisions:
  - "Achievement data class defined in TaskViewModel.kt file (same package as UI) — not in data layer — because it is a UI domain model, not a persistence entity"
  - "checkAndUnlockAchievements called from inside toggleTaskCompletion after repository call using repository.allTasks.first() snapshot"
  - "sessionStartedAt set at very start of saveSession coroutine body before clearAllPriorities()"
  - "Speed-run window: lastCompletedAt - sessionStartedAt <= 3_600_000L (1 hour)"
metrics:
  duration_minutes: 10
  completed_date: "2026-03-23"
  tasks_completed: 2
  files_changed: 2
---

# Phase 11 Plan 02: Achievement ViewModel Logic Summary

**One-liner:** Achievement domain model, 8-definition catalog, streak/volume/speed-run unlock logic, and newlyUnlockedAchievement StateFlow added to TaskViewModel with AchievementDao injected via Koin.

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | Achievement domain model and ViewModel constructor update | 4e4c608 | TaskViewModel.kt, KoinModule.kt |
| 2 | Streak calculation, speed run detection, and achievement check logic | 4e4c608 | TaskViewModel.kt |

## Deviations from Plan

None - plan executed exactly as written.

## Self-Check: PASSED

- Achievement data class: FOUND in TaskViewModel.kt
- ACHIEVEMENT_DEFINITIONS companion object: FOUND
- AchievementDao constructor param: FOUND
- sessionStartedAt field: FOUND
- _newlyUnlockedAchievement StateFlow: FOUND
- _achievements StateFlow: FOUND
- calculateStreak(): FOUND
- loadAchievements(): FOUND
- checkAndUnlockAchievements(): FOUND
- dismissAchievementUnlock(): FOUND
- toggleTaskCompletion calls checkAndUnlockAchievements: FOUND
- saveSession sets sessionStartedAt: FOUND
- KoinModule 4 args: FOUND
- Build: SUCCESS
