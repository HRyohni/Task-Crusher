package hr.fipu.organizationtool.ui

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fipu.organizationtool.data.AchievementDao
import hr.fipu.organizationtool.data.AchievementEntity
import hr.fipu.organizationtool.data.OnboardingRepository
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.data.TaskRepository
import hr.fipu.organizationtool.widget.ZenStackWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import android.content.Context
import org.json.JSONArray

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long?,
    val progress: Int,
    val goal: Int
)

class TaskViewModel(
    private val repository: TaskRepository,
    private val onboardingRepository: OnboardingRepository,
    private val achievementDao: AchievementDao,
    private val application: Application
) : ViewModel() {

    companion object {
        val ACHIEVEMENT_DEFINITIONS = listOf(
            Achievement("first_complete", "First Step", "Complete your first task", false, null, 0, 1),
            Achievement("ten_tasks",      "Getting Started", "Complete 10 tasks",    false, null, 0, 10),
            Achievement("fifty_tasks",    "Momentum",        "Complete 50 tasks",    false, null, 0, 50),
            Achievement("hundred_tasks",  "Century",         "Complete 100 tasks",   false, null, 0, 100),
            Achievement("streak_3",       "On a Roll",       "3-day streak",         false, null, 0, 3),
            Achievement("streak_7",       "Week Warrior",    "7-day streak",         false, null, 0, 7),
            Achievement("streak_30",      "Unstoppable",     "30-day streak",        false, null, 0, 30),
            Achievement("speed_run",      "Speed Run",       "Complete all 3 priorities within 1 hour", false, null, 0, 1)
        )
    }

    val savedTasks: StateFlow<List<Task>?> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val suggestedTasks: StateFlow<List<Task>> = repository.suggestedTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasSeenBackTapGuide: StateFlow<Boolean> = onboardingRepository.hasSeenBackTapGuide
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _brainDumpTasks = MutableStateFlow<List<Task>>(emptyList())
    val brainDumpTasks: StateFlow<List<Task>> = _brainDumpTasks.asStateFlow()

    private val _selectedPriorityIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedPriorityIds: StateFlow<Set<Int>> = _selectedPriorityIds.asStateFlow()

    // Calendar: last 90 days of completion counts, date -> task count
    private val _completionHistory = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val completionHistory: StateFlow<Map<LocalDate, Int>> = _completionHistory.asStateFlow()

    // Calendar: which day the user has tapped for detail view
    private val _selectedDay = MutableStateFlow<LocalDate?>(null)
    val selectedDay: StateFlow<LocalDate?> = _selectedDay.asStateFlow()

    // Calendar: tasks completed on the selected day
    val tasksForSelectedDay: StateFlow<List<Task>> = _selectedDay
        .flatMapLatest { date ->
            if (date == null) {
                flowOf(emptyList())
            } else {
                val zoneId = ZoneId.systemDefault()
                val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
                val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
                repository.getTasksCompletedOn(startOfDay, endOfDay)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In-memory session start time, set when saveSession() is called
    private var sessionStartedAt: Long = 0L

    // Emits the achievement just unlocked; UI observes and clears after showing
    private val _newlyUnlockedAchievement = MutableStateFlow<Achievement?>(null)
    val newlyUnlockedAchievement: StateFlow<Achievement?> = _newlyUnlockedAchievement.asStateFlow()

    // All achievements with live unlock/progress state
    private val _achievements = MutableStateFlow<List<Achievement>>(ACHIEVEMENT_DEFINITIONS)
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private var nextTempId = -1

    private val draftPrefs by lazy {
        application.getSharedPreferences("zen_draft", Context.MODE_PRIVATE)
    }

    private fun saveDraftTasks() {
        val array = JSONArray()
        _brainDumpTasks.value.forEach { array.put(it.title) }
        draftPrefs.edit().putString("draft_tasks", array.toString()).apply()
    }

    private fun loadDraftTasks() {
        val raw = draftPrefs.getString("draft_tasks", null) ?: return
        val array = JSONArray(raw)
        val tasks = (0 until array.length()).map { i ->
            Task(id = nextTempId--, title = array.getString(i))
        }
        if (tasks.isNotEmpty()) {
            _brainDumpTasks.value = tasks
        }
    }

    private fun calculateStreak(tasks: List<Task>): Int {
        val zoneId = ZoneId.systemDefault()
        val completedDays = tasks
            .mapNotNull { it.completedAt }
            .map { epochMillis ->
                java.time.Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
            }
            .toSet()

        var streak = 0
        var day = LocalDate.now()
        while (completedDays.contains(day)) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }

    private suspend fun loadAchievements(allTasks: List<Task>): List<Achievement> {
        val unlockedEntities = achievementDao.getAllUnlocked().associateBy { it.id }
        val totalCompleted = allTasks.count { it.status == "COMPLETED" }
        val streak = calculateStreak(allTasks)

        return ACHIEVEMENT_DEFINITIONS.map { def ->
            val entity = unlockedEntities[def.id]
            val progress = when (def.id) {
                "first_complete"  -> minOf(totalCompleted, 1)
                "ten_tasks"       -> minOf(totalCompleted, 10)
                "fifty_tasks"     -> minOf(totalCompleted, 50)
                "hundred_tasks"   -> minOf(totalCompleted, 100)
                "streak_3"        -> minOf(streak, 3)
                "streak_7"        -> minOf(streak, 7)
                "streak_30"       -> minOf(streak, 30)
                "speed_run"       -> if (entity != null) 1 else 0
                else              -> 0
            }
            def.copy(
                isUnlocked = entity != null,
                unlockedAt = entity?.unlockedAt,
                progress = progress
            )
        }
    }

    private fun checkAndUnlockAchievements(allTasks: List<Task>) {
        viewModelScope.launch {
            val totalCompleted = allTasks.count { it.status == "COMPLETED" }
            val streak = calculateStreak(allTasks)
            val alreadyUnlocked = achievementDao.getAllUnlocked().map { it.id }.toSet()

            val volumeUnlocks = mapOf(
                "first_complete" to (totalCompleted >= 1),
                "ten_tasks"      to (totalCompleted >= 10),
                "fifty_tasks"    to (totalCompleted >= 50),
                "hundred_tasks"  to (totalCompleted >= 100)
            )
            val streakUnlocks = mapOf(
                "streak_3"  to (streak >= 3),
                "streak_7"  to (streak >= 7),
                "streak_30" to (streak >= 30)
            )

            val candidates = volumeUnlocks + streakUnlocks
            var firstNew: Achievement? = null

            for ((id, earned) in candidates) {
                if (earned && id !in alreadyUnlocked) {
                    val now = System.currentTimeMillis()
                    achievementDao.insert(AchievementEntity(id, now))
                    if (firstNew == null) {
                        firstNew = ACHIEVEMENT_DEFINITIONS.first { it.id == id }
                            .copy(isUnlocked = true, unlockedAt = now, progress = 1)
                    }
                }
            }

            // Speed run check: all 3 priorities completed within 1 hour of sessionStartedAt
            if ("speed_run" !in alreadyUnlocked && sessionStartedAt > 0L) {
                val priorityTasks = allTasks.filter { it.isPriority }
                val allPrioritiesDone = priorityTasks.size == 3 && priorityTasks.all { it.status == "COMPLETED" }
                if (allPrioritiesDone) {
                    val lastCompletedAt = priorityTasks.mapNotNull { it.completedAt }.maxOrNull() ?: 0L
                    if (lastCompletedAt - sessionStartedAt <= 3_600_000L) {
                        val now = System.currentTimeMillis()
                        achievementDao.insert(AchievementEntity("speed_run", now))
                        if (firstNew == null) {
                            firstNew = ACHIEVEMENT_DEFINITIONS.first { it.id == "speed_run" }
                                .copy(isUnlocked = true, unlockedAt = now, progress = 1)
                        }
                    }
                }
            }

            // Refresh achievements list
            _achievements.value = loadAchievements(allTasks)

            // Signal UI overlay (only the first newly unlocked this check cycle)
            if (firstNew != null) {
                _newlyUnlockedAchievement.value = firstNew
            }
        }
    }

    fun dismissAchievementUnlock() {
        _newlyUnlockedAchievement.value = null
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        val newTask = Task(id = nextTempId--, title = title)
        _brainDumpTasks.value = listOf(newTask) + _brainDumpTasks.value
        saveDraftTasks()
    }

    fun addSuggestedTask(task: Task) {
        if (_brainDumpTasks.value.none { it.id == task.id }) {
            _brainDumpTasks.value = listOf(task) + _brainDumpTasks.value
            saveDraftTasks()
        }
    }

    fun removeTask(task: Task) {
        _brainDumpTasks.value = _brainDumpTasks.value - task
        _selectedPriorityIds.value = _selectedPriorityIds.value - task.id
        saveDraftTasks()
    }

    fun togglePriority(task: Task) {
        val currentSelected = _selectedPriorityIds.value
        if (currentSelected.contains(task.id)) {
            _selectedPriorityIds.value = currentSelected - task.id
        } else if (currentSelected.size < 3) {
            _selectedPriorityIds.value = currentSelected + task.id
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
            ZenStackWidget().updateAll(application)
            val allTasks = repository.allTasks.first()
            checkAndUnlockAchievements(allTasks)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            ZenStackWidget().updateAll(application)
        }
    }

    fun markBackTapGuideAsSeen() {
        viewModelScope.launch {
            onboardingRepository.setHasSeenBackTapGuide(true)
        }
    }

    fun selectDay(date: LocalDate) {
        _selectedDay.value = date
    }

    fun loadCompletionHistory() {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now()
            val historyMap = mutableMapOf<LocalDate, Int>()
            for (daysBack in 0L until 90L) {
                val day = today.minusDays(daysBack)
                val startOfDay = day.atStartOfDay(zoneId).toInstant().toEpochMilli()
                val endOfDay = day.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
                val tasks = repository.getTasksCompletedOn(startOfDay, endOfDay).first()
                historyMap[day] = tasks.size
            }
            _completionHistory.value = historyMap
        }
    }

    init {
        loadDraftTasks()
        loadCompletionHistory()
        viewModelScope.launch {
            val allTasks = repository.allTasks.first()
            _achievements.value = loadAchievements(allTasks)
        }
    }

    fun saveSession() {
        viewModelScope.launch {
            sessionStartedAt = System.currentTimeMillis()
            repository.clearAllPriorities()
            val finalTasks = _brainDumpTasks.value.map { task ->
                val isPriority = _selectedPriorityIds.value.contains(task.id)
                if (task.id < 0) {
                    task.copy(id = 0, isPriority = isPriority)
                } else {
                    task.copy(isPriority = isPriority)
                }
            }
            finalTasks.forEach { repository.insertTask(it) }
            ZenStackWidget().updateAll(application)
            _brainDumpTasks.value = emptyList()
            _selectedPriorityIds.value = emptySet()
            draftPrefs.edit().remove("draft_tasks").apply()
        }
    }
}
