package hr.fipu.organizationtool.ui

import android.Manifest
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fipu.organizationtool.MainActivity
import hr.fipu.organizationtool.data.AchievementDao
import hr.fipu.organizationtool.data.AchievementEntity

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
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
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
    private val achievementDao: AchievementDao,
    private val application: Application
) : ViewModel() {

    companion object {
        val ACHIEVEMENT_DEFINITIONS = listOf(
            // ── Volume ──────────────────────────────────────────────────────────
            Achievement("first_complete",  "First Step",          "Complete your first task",          false, null, 0, 1),
            Achievement("tasks_3",         "Tricycle",            "Complete 3 tasks",                  false, null, 0, 3),
            Achievement("tasks_5",         "Baby Steps",          "Complete 5 tasks",                  false, null, 0, 5),
            Achievement("ten_tasks",       "Getting Started",     "Complete 10 tasks",                 false, null, 0, 10),
            Achievement("tasks_25",        "Quarter Century",     "Complete 25 tasks",                 false, null, 0, 25),
            Achievement("fifty_tasks",     "Momentum",            "Complete 50 tasks",                 false, null, 0, 50),
            Achievement("tasks_75",        "Growing Strong",      "Complete 75 tasks",                 false, null, 0, 75),
            Achievement("hundred_tasks",   "Century",             "Complete 100 tasks",                false, null, 0, 100),
            Achievement("tasks_150",       "Overachiever",        "Complete 150 tasks",                false, null, 0, 150),
            Achievement("tasks_200",       "Taskmaster",          "Complete 200 tasks",                false, null, 0, 200),
            Achievement("tasks_300",       "Three Hundred",       "Complete 300 tasks",                false, null, 0, 300),
            Achievement("tasks_365",       "Year of Tasks",       "Complete 365 tasks",                false, null, 0, 365),
            Achievement("tasks_500",       "Five Hundred Strong", "Complete 500 tasks",                false, null, 0, 500),
            Achievement("tasks_750",       "Three Quarters",      "Complete 750 tasks",                false, null, 0, 750),
            Achievement("tasks_1000",      "Task Legend",         "Complete 1000 tasks",               false, null, 0, 1000),
            // ── Streaks ─────────────────────────────────────────────────────────
            Achievement("streak_2",   "Two in a Row",       "2-day streak",    false, null, 0, 2),
            Achievement("streak_3",   "On a Roll",          "3-day streak",    false, null, 0, 3),
            Achievement("streak_5",   "Habit Forming",      "5-day streak",    false, null, 0, 5),
            Achievement("streak_7",   "Week Warrior",       "7-day streak",    false, null, 0, 7),
            Achievement("streak_10",  "Ten Day Run",        "10-day streak",   false, null, 0, 10),
            Achievement("streak_14",  "Two Weeks Solid",    "14-day streak",   false, null, 0, 14),
            Achievement("streak_15",  "Fifteen",            "15-day streak",   false, null, 0, 15),
            Achievement("streak_21",  "Three Weeks",        "21-day streak",   false, null, 0, 21),
            Achievement("streak_30",  "Unstoppable",        "30-day streak",   false, null, 0, 30),
            Achievement("streak_45",  "Forty Five",         "45-day streak",   false, null, 0, 45),
            Achievement("streak_60",  "Two Month March",    "60-day streak",   false, null, 0, 60),
            Achievement("streak_100", "Century Streak",     "100-day streak",  false, null, 0, 100),
            Achievement("streak_180", "Half Year",          "180-day streak",  false, null, 0, 180),
            Achievement("streak_365", "Year Streak",        "365-day streak",  false, null, 0, 365),
            // ── Perfect priority streaks ────────────────────────────────────────
            Achievement("perfect_3",    "Triple Perfect",     "All priorities 3 days in a row",   false, null, 0, 3),
            Achievement("perfect_5",    "High Five",          "All priorities 5 days in a row",   false, null, 0, 5),
            Achievement("perfect_week", "Perfect Week",       "All priorities 7 days in a row",   false, null, 0, 7),
            Achievement("perfect_10",   "Ten Perfect",        "All priorities 10 days in a row",  false, null, 0, 10),
            Achievement("perfect_14",   "Iron Will",          "All priorities 14 days in a row",  false, null, 0, 14),
            Achievement("perfect_21",   "Relentless",         "All priorities 21 days in a row",  false, null, 0, 21),
            Achievement("perfect_30",   "Perfect Month",      "All priorities 30 days in a row",  false, null, 0, 30),
            Achievement("perfect_45",   "Six Week Legend",    "All priorities 45 days in a row",  false, null, 0, 45),
            Achievement("perfect_60",   "Two Month Perfect",  "All priorities 60 days in a row",  false, null, 0, 60),
            Achievement("perfect_100",  "Century Perfect",    "All priorities 100 days in a row", false, null, 0, 100),
            // ── Priority tasks total ────────────────────────────────────────────
            Achievement("first_priority", "Priority Rookie",  "Complete your first priority task",   false, null, 0, 1),
            Achievement("priority_10",    "Priority Pro I",   "Complete 10 priority tasks",          false, null, 0, 10),
            Achievement("priority_25",    "Priority Pro II",  "Complete 25 priority tasks",          false, null, 0, 25),
            Achievement("priority_50",    "Priority Pro III", "Complete 50 priority tasks",          false, null, 0, 50),
            Achievement("priority_100",   "Priority Master",  "Complete 100 priority tasks",         false, null, 0, 100),
            Achievement("priority_200",   "Priority Expert",  "Complete 200 priority tasks",         false, null, 0, 200),
            Achievement("priority_300",   "Priority Legend",  "Complete 300 priority tasks",         false, null, 0, 300),
            Achievement("priority_500",   "Priority God",     "Complete 500 priority tasks",         false, null, 0, 500),
            // ── Brain dump ──────────────────────────────────────────────────────
            Achievement("braindump_1",   "Brain Initiate",   "Complete your first brain dump task", false, null, 0, 1),
            Achievement("braindump_10",  "Clear Headed I",   "Complete 10 brain dump tasks",        false, null, 0, 10),
            Achievement("braindump_50",  "Clear Headed II",  "Complete 50 brain dump tasks",        false, null, 0, 50),
            Achievement("braindump_100", "Clear Headed III", "Complete 100 brain dump tasks",       false, null, 0, 100),
            Achievement("braindump_250", "Brain Drain",      "Complete 250 brain dump tasks",       false, null, 0, 250),
            Achievement("braindump_500", "Master Clearer",   "Complete 500 brain dump tasks",       false, null, 0, 500),
            // ── Active days ─────────────────────────────────────────────────────
            Achievement("active_7",   "Week Active",        "Active on 7 different days",   false, null, 0, 7),
            Achievement("active_14",  "Two Weeks Active",   "Active on 14 different days",  false, null, 0, 14),
            Achievement("active_30",  "Monthly Active",     "Active on 30 different days",  false, null, 0, 30),
            Achievement("active_60",  "Two Months Active",  "Active on 60 different days",  false, null, 0, 60),
            Achievement("active_90",  "Quarterly Active",   "Active on 90 different days",  false, null, 0, 90),
            Achievement("active_180", "Half Year Active",   "Active on 180 different days", false, null, 0, 180),
            Achievement("active_365", "Year Active",        "Active on 365 different days", false, null, 0, 365),
            // ── Perfect session count ───────────────────────────────────────────
            Achievement("perfect_sessions_5",   "Five Star",        "5 days with all priorities done",   false, null, 0, 5),
            Achievement("perfect_sessions_10",  "Perfect Ten",      "10 days with all priorities done",  false, null, 0, 10),
            Achievement("perfect_sessions_25",  "Silver Standard",  "25 days with all priorities done",  false, null, 0, 25),
            Achievement("perfect_sessions_50",  "Gold Standard",    "50 days with all priorities done",  false, null, 0, 50),
            Achievement("perfect_sessions_75",  "Platinum Standard","75 days with all priorities done",  false, null, 0, 75),
            Achievement("perfect_sessions_100", "Diamond Standard", "100 days with all priorities done", false, null, 0, 100),
            // ── Daily volume ────────────────────────────────────────────────────
            Achievement("productive_day", "Productive Day", "Complete 5+ tasks in one day",  false, null, 0, 1),
            Achievement("power_day",      "Power Day",      "Complete 10+ tasks in one day", false, null, 0, 1),
            Achievement("machine_day",    "The Machine",    "Complete 15+ tasks in one day", false, null, 0, 1),
            Achievement("super_day",      "Super Human",    "Complete 20+ tasks in one day", false, null, 0, 1),
            Achievement("marathon_day",   "Marathon Day",   "Complete 25+ tasks in one day", false, null, 0, 1),
            // ── Speed ────────────────────────────────────────────────────────────
            Achievement("speed_run",    "Speed Run",   "All priorities within 1 hour",    false, null, 0, 1),
            Achievement("lightning",    "Lightning",   "All priorities within 30 minutes", false, null, 0, 1),
            Achievement("blitz",        "Blitz Mode",  "All priorities within 15 minutes", false, null, 0, 1),
            Achievement("flash",        "Flash",       "All priorities within 5 minutes",  false, null, 0, 1),
            Achievement("speed_run_5",  "Speed Demon", "All priorities within 1 hour 5 times",  false, null, 0, 5),
            Achievement("speed_run_10", "Efficiency Expert", "All priorities within 1 hour 10 times", false, null, 0, 10),
            // ── Time of day ─────────────────────────────────────────────────────
            Achievement("dawn_patrol",    "Dawn Patrol",      "All priorities before 6am",        false, null, 0, 1),
            Achievement("morning_pro",    "Early Champion",   "All priorities before 8am",        false, null, 0, 1),
            Achievement("morning_rush",   "Morning Rush",     "All priorities before 9am",        false, null, 0, 1),
            Achievement("early_bird",     "Early Bird",       "All priorities before noon",        false, null, 0, 1),
            Achievement("lunch_legend",   "Lunch Legend",     "All priorities between 12pm–2pm",  false, null, 0, 1),
            Achievement("golden_hour",    "Golden Hour",      "All priorities between 5pm–7pm",   false, null, 0, 1),
            Achievement("night_shift",    "Night Shift",      "All priorities after 10pm",         false, null, 0, 1),
            Achievement("night_owl",      "Night Owl",        "All priorities after 9pm",          false, null, 0, 1),
            Achievement("midnight_grind", "Midnight Grind",   "Complete any task between midnight and 2am", false, null, 0, 1),
            Achievement("early_bird_5",   "Early Riser",      "All priorities before noon 5 times",  false, null, 0, 5),
            Achievement("early_bird_10",  "Morning Champion", "All priorities before noon 10 times", false, null, 0, 10),
            Achievement("night_owl_5",    "Night Owl Pack",   "All priorities after 9pm 5 times",    false, null, 0, 5),
            Achievement("night_owl_10",   "Nocturnal Master", "All priorities after 9pm 10 times",   false, null, 0, 10),
            // ── Day of week ─────────────────────────────────────────────────────
            Achievement("monday_momentum", "Monday Momentum", "All priorities on a Monday",    false, null, 0, 1),
            Achievement("hump_day",        "Hump Day Hero",   "All priorities on a Wednesday", false, null, 0, 1),
            Achievement("friday_finisher", "Friday Finisher", "All priorities on a Friday",    false, null, 0, 1),
            Achievement("saturday_grind",  "Saturday Grind",  "All priorities on a Saturday",  false, null, 0, 1),
            Achievement("sunday_prep",     "Sunday Prep",     "All priorities on a Sunday",    false, null, 0, 1),
            Achievement("weekend_warrior", "Weekend Warrior", "All priorities on both Sat and Sun in same week", false, null, 0, 1),
            // ── Comeback ────────────────────────────────────────────────────────
            Achievement("comeback_kid", "Comeback Kid",   "Return after 7+ day gap and complete all priorities", false, null, 0, 1),
            Achievement("comeback_3",   "Resilient",      "Make a comeback 3 times",                             false, null, 0, 3),
            Achievement("comeback_5",   "Truly Resilient","Make a comeback 5 times",                             false, null, 0, 5)
        )
    }

    val savedTasks: StateFlow<List<Task>?> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val suggestedTasks: StateFlow<List<Task>> = repository.suggestedTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    // Calendar: tasks scheduled on the selected day
    val scheduledTasksForSelectedDay: StateFlow<List<Task>> = _selectedDay
        .flatMapLatest { date ->
            if (date == null) {
                flowOf(emptyList())
            } else {
                val zoneId = ZoneId.systemDefault()
                val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
                val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
                repository.getTasksScheduledOn(startOfDay, endOfDay)
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

    private fun calculatePriorityStreak(tasks: List<Task>): Int {
        val zoneId = ZoneId.systemDefault()
        var streak = 0
        var day = LocalDate.now()
        while (true) {
            val start = day.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val end = day.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
            val done = tasks.count { t ->
                t.isPriority && t.status == "COMPLETED" && t.completedAt != null && t.completedAt in start..end
            }
            if (done >= 3) { streak++; day = day.minusDays(1) } else break
        }
        return streak
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
        val unlocked = achievementDao.getAllUnlocked().associateBy { it.id }
        val zoneId = ZoneId.systemDefault()
        val totalCompleted = allTasks.count { it.status == "COMPLETED" }
        val totalPriority = allTasks.count { it.isPriority && it.status == "COMPLETED" }
        val totalBrainDump = allTasks.count { !it.isPriority && it.status == "COMPLETED" }
        val streak = calculateStreak(allTasks)
        val pStreak = calculatePriorityStreak(allTasks)
        val ct = allTasks.filter { it.status == "COMPLETED" && it.completedAt != null }
        val byDay = ct.groupBy { Instant.ofEpochMilli(it.completedAt!!).atZone(zoneId).toLocalDate() }
        val activeDays = byDay.size
        val perfectDays = byDay.values.count { t -> t.count { it.isPriority } >= 3 }
        val maxDay = byDay.values.maxOfOrNull { it.size } ?: 0
        val ebDays = byDay.values.count { t -> t.count { x -> x.isPriority && x.completedAt != null && Instant.ofEpochMilli(x.completedAt).atZone(zoneId).toLocalTime().hour < 12 } >= 3 }
        val noDays = byDay.values.count { t -> t.count { x -> x.isPriority && x.completedAt != null && Instant.ofEpochMilli(x.completedAt).atZone(zoneId).toLocalTime().hour >= 21 } >= 3 }
        val srDays = byDay.values.count { t -> val times = t.filter { it.isPriority }.mapNotNull { it.completedAt }; times.size >= 3 && (times.max() - times.min()) <= 3_600_000L }

        fun prog(id: String): Int = when (id) {
            "first_complete" -> minOf(totalCompleted, 1)
            "tasks_3"    -> minOf(totalCompleted, 3);   "tasks_5"   -> minOf(totalCompleted, 5)
            "ten_tasks"  -> minOf(totalCompleted, 10);  "tasks_25"  -> minOf(totalCompleted, 25)
            "fifty_tasks"-> minOf(totalCompleted, 50);  "tasks_75"  -> minOf(totalCompleted, 75)
            "hundred_tasks" -> minOf(totalCompleted, 100); "tasks_150" -> minOf(totalCompleted, 150)
            "tasks_200"  -> minOf(totalCompleted, 200); "tasks_300" -> minOf(totalCompleted, 300)
            "tasks_365"  -> minOf(totalCompleted, 365); "tasks_500" -> minOf(totalCompleted, 500)
            "tasks_750"  -> minOf(totalCompleted, 750); "tasks_1000"-> minOf(totalCompleted, 1000)
            "streak_2"   -> minOf(streak, 2);   "streak_3"   -> minOf(streak, 3)
            "streak_5"   -> minOf(streak, 5);   "streak_7"   -> minOf(streak, 7)
            "streak_10"  -> minOf(streak, 10);  "streak_14"  -> minOf(streak, 14)
            "streak_15"  -> minOf(streak, 15);  "streak_21"  -> minOf(streak, 21)
            "streak_30"  -> minOf(streak, 30);  "streak_45"  -> minOf(streak, 45)
            "streak_60"  -> minOf(streak, 60);  "streak_100" -> minOf(streak, 100)
            "streak_180" -> minOf(streak, 180); "streak_365" -> minOf(streak, 365)
            "perfect_3"  -> minOf(pStreak, 3);  "perfect_5"  -> minOf(pStreak, 5)
            "perfect_week"-> minOf(pStreak, 7); "perfect_10" -> minOf(pStreak, 10)
            "perfect_14" -> minOf(pStreak, 14); "perfect_21" -> minOf(pStreak, 21)
            "perfect_30" -> minOf(pStreak, 30); "perfect_45" -> minOf(pStreak, 45)
            "perfect_60" -> minOf(pStreak, 60); "perfect_100"-> minOf(pStreak, 100)
            "first_priority" -> minOf(totalPriority, 1)
            "priority_10"  -> minOf(totalPriority, 10);  "priority_25" -> minOf(totalPriority, 25)
            "priority_50"  -> minOf(totalPriority, 50);  "priority_100"-> minOf(totalPriority, 100)
            "priority_200" -> minOf(totalPriority, 200); "priority_300"-> minOf(totalPriority, 300)
            "priority_500" -> minOf(totalPriority, 500)
            "braindump_1"  -> minOf(totalBrainDump, 1);  "braindump_10" -> minOf(totalBrainDump, 10)
            "braindump_50" -> minOf(totalBrainDump, 50); "braindump_100"-> minOf(totalBrainDump, 100)
            "braindump_250"-> minOf(totalBrainDump, 250);"braindump_500"-> minOf(totalBrainDump, 500)
            "active_7"  -> minOf(activeDays, 7);   "active_14" -> minOf(activeDays, 14)
            "active_30" -> minOf(activeDays, 30);  "active_60" -> minOf(activeDays, 60)
            "active_90" -> minOf(activeDays, 90);  "active_180"-> minOf(activeDays, 180)
            "active_365"-> minOf(activeDays, 365)
            "perfect_sessions_5"  -> minOf(perfectDays, 5);  "perfect_sessions_10" -> minOf(perfectDays, 10)
            "perfect_sessions_25" -> minOf(perfectDays, 25); "perfect_sessions_50" -> minOf(perfectDays, 50)
            "perfect_sessions_75" -> minOf(perfectDays, 75); "perfect_sessions_100"-> minOf(perfectDays, 100)
            "productive_day" -> if (maxDay >= 5) 1 else 0;  "power_day"    -> if (maxDay >= 10) 1 else 0
            "machine_day"    -> if (maxDay >= 15) 1 else 0; "super_day"    -> if (maxDay >= 20) 1 else 0
            "marathon_day"   -> if (maxDay >= 25) 1 else 0
            "speed_run_5"  -> minOf(srDays, 5);  "speed_run_10" -> minOf(srDays, 10)
            "early_bird_5" -> minOf(ebDays, 5);  "early_bird_10"-> minOf(ebDays, 10)
            "night_owl_5"  -> minOf(noDays, 5);  "night_owl_10" -> minOf(noDays, 10)
            else -> if (unlocked.containsKey(id)) 1 else 0
        }
        return ACHIEVEMENT_DEFINITIONS.map { def ->
            val entity = unlocked[def.id]
            def.copy(isUnlocked = entity != null, unlockedAt = entity?.unlockedAt, progress = prog(def.id))
        }
    }

    private fun checkAndUnlockAchievements(allTasks: List<Task>) {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now()
            val alreadyUnlocked = achievementDao.getAllUnlocked().map { it.id }.toSet()

            // ── Precompute ───────────────────────────────────────────────────────
            val totalCompleted  = allTasks.count { it.status == "COMPLETED" }
            val totalPriority   = allTasks.count { it.isPriority && it.status == "COMPLETED" }
            val totalBrainDump  = allTasks.count { !it.isPriority && it.status == "COMPLETED" }
            val streak          = calculateStreak(allTasks)
            val pStreak         = calculatePriorityStreak(allTasks)
            val ct              = allTasks.filter { it.status == "COMPLETED" && it.completedAt != null }
            val byDay           = ct.groupBy { Instant.ofEpochMilli(it.completedAt!!).atZone(zoneId).toLocalDate() }
            val activeDays      = byDay.size
            val perfectDays     = byDay.values.count { t -> t.count { it.isPriority } >= 3 }
            val maxDay          = byDay.values.maxOfOrNull { it.size } ?: 0
            val ebDays          = byDay.values.count { t -> t.count { x -> x.isPriority && x.completedAt != null && Instant.ofEpochMilli(x.completedAt).atZone(zoneId).toLocalTime().hour < 12 } >= 3 }
            val noDays          = byDay.values.count { t -> t.count { x -> x.isPriority && x.completedAt != null && Instant.ofEpochMilli(x.completedAt).atZone(zoneId).toLocalTime().hour >= 21 } >= 3 }
            val srDays          = byDay.values.count { t -> val times = t.filter { it.isPriority }.mapNotNull { it.completedAt }; times.size >= 3 && (times.max() - times.min()) <= 3_600_000L }

            val startOfToday = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val endOfToday   = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
            val todayTasks   = ct.filter { it.completedAt!! in startOfToday..endOfToday }
            val todayPriDone = todayTasks.count { it.isPriority }
            val allPriDoneToday = todayPriDone >= 3

            // Priority completions today by hour
            fun todayPriHour(from: Int, to: Int) = allPriDoneToday &&
                todayTasks.filter { it.isPriority }.all { x ->
                    Instant.ofEpochMilli(x.completedAt!!).atZone(zoneId).toLocalTime().hour.let { h -> h in from until to }
                }
            fun todayPriAfter(h: Int) = allPriDoneToday &&
                todayTasks.filter { it.isPriority }.all { x ->
                    Instant.ofEpochMilli(x.completedAt!!).atZone(zoneId).toLocalTime().hour >= h
                }
            fun todayPriBefore(h: Int) = allPriDoneToday &&
                todayTasks.filter { it.isPriority }.all { x ->
                    Instant.ofEpochMilli(x.completedAt!!).atZone(zoneId).toLocalTime().hour < h
                }

            val dayOfWeek = today.dayOfWeek
            val midnightGrind = todayTasks.any { x ->
                Instant.ofEpochMilli(x.completedAt!!).atZone(zoneId).toLocalTime().hour < 2
            }

            // Weekend warrior: both Sat and Sun of current week had all 3 priorities done
            val mondayOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            val sat = mondayOfWeek.plusDays(5)
            val sun = mondayOfWeek.plusDays(6)
            fun dayHad3(d: LocalDate): Boolean {
                val s = d.atStartOfDay(zoneId).toInstant().toEpochMilli()
                val e = d.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
                return ct.count { it.isPriority && it.completedAt!! in s..e } >= 3
            }
            val weekendWarrior = dayHad3(sat) && dayHad3(sun)

            // Comeback counting: days where all 3 priorities done AND 7+ day gap before
            val sortedPerfectDates = byDay.entries
                .filter { (_, t) -> t.count { it.isPriority } >= 3 }
                .map { it.key }
                .sorted()
            var comebackCount = 0
            for (i in sortedPerfectDates.indices) {
                val d = sortedPerfectDates[i]
                val lastAnyActivity = ct
                    .mapNotNull { it.completedAt }
                    .filter { it < d.atStartOfDay(zoneId).toInstant().toEpochMilli() }
                    .maxOrNull()
                if (lastAnyActivity != null) {
                    val lastDate = Instant.ofEpochMilli(lastAnyActivity).atZone(zoneId).toLocalDate()
                    if (ChronoUnit.DAYS.between(lastDate, d) >= 7) comebackCount++
                }
            }

            // Speed run: from sessionStartedAt to last priority completed
            fun speedCheck(maxMs: Long): Boolean {
                if (sessionStartedAt <= 0L) return false
                val pTasks = allTasks.filter { it.isPriority }
                if (pTasks.size < 3 || !pTasks.all { it.status == "COMPLETED" }) return false
                val last = pTasks.mapNotNull { it.completedAt }.maxOrNull() ?: 0L
                return last - sessionStartedAt <= maxMs
            }

            // ── Simple threshold conditions map ──────────────────────────────────
            val conditions = mutableMapOf(
                // Volume
                "first_complete"  to (totalCompleted >= 1),
                "tasks_3"         to (totalCompleted >= 3),
                "tasks_5"         to (totalCompleted >= 5),
                "ten_tasks"       to (totalCompleted >= 10),
                "tasks_25"        to (totalCompleted >= 25),
                "fifty_tasks"     to (totalCompleted >= 50),
                "tasks_75"        to (totalCompleted >= 75),
                "hundred_tasks"   to (totalCompleted >= 100),
                "tasks_150"       to (totalCompleted >= 150),
                "tasks_200"       to (totalCompleted >= 200),
                "tasks_300"       to (totalCompleted >= 300),
                "tasks_365"       to (totalCompleted >= 365),
                "tasks_500"       to (totalCompleted >= 500),
                "tasks_750"       to (totalCompleted >= 750),
                "tasks_1000"      to (totalCompleted >= 1000),
                // Streaks
                "streak_2"        to (streak >= 2),
                "streak_3"        to (streak >= 3),
                "streak_5"        to (streak >= 5),
                "streak_7"        to (streak >= 7),
                "streak_10"       to (streak >= 10),
                "streak_14"       to (streak >= 14),
                "streak_15"       to (streak >= 15),
                "streak_21"       to (streak >= 21),
                "streak_30"       to (streak >= 30),
                "streak_45"       to (streak >= 45),
                "streak_60"       to (streak >= 60),
                "streak_100"      to (streak >= 100),
                "streak_180"      to (streak >= 180),
                "streak_365"      to (streak >= 365),
                // Perfect priority streaks
                "perfect_3"       to (pStreak >= 3),
                "perfect_5"       to (pStreak >= 5),
                "perfect_week"    to (pStreak >= 7),
                "perfect_10"      to (pStreak >= 10),
                "perfect_14"      to (pStreak >= 14),
                "perfect_21"      to (pStreak >= 21),
                "perfect_30"      to (pStreak >= 30),
                "perfect_45"      to (pStreak >= 45),
                "perfect_60"      to (pStreak >= 60),
                "perfect_100"     to (pStreak >= 100),
                // Priority total
                "first_priority"  to (totalPriority >= 1),
                "priority_10"     to (totalPriority >= 10),
                "priority_25"     to (totalPriority >= 25),
                "priority_50"     to (totalPriority >= 50),
                "priority_100"    to (totalPriority >= 100),
                "priority_200"    to (totalPriority >= 200),
                "priority_300"    to (totalPriority >= 300),
                "priority_500"    to (totalPriority >= 500),
                // Brain dump
                "braindump_1"     to (totalBrainDump >= 1),
                "braindump_10"    to (totalBrainDump >= 10),
                "braindump_50"    to (totalBrainDump >= 50),
                "braindump_100"   to (totalBrainDump >= 100),
                "braindump_250"   to (totalBrainDump >= 250),
                "braindump_500"   to (totalBrainDump >= 500),
                // Active days
                "active_7"        to (activeDays >= 7),
                "active_14"       to (activeDays >= 14),
                "active_30"       to (activeDays >= 30),
                "active_60"       to (activeDays >= 60),
                "active_90"       to (activeDays >= 90),
                "active_180"      to (activeDays >= 180),
                "active_365"      to (activeDays >= 365),
                // Perfect sessions
                "perfect_sessions_5"   to (perfectDays >= 5),
                "perfect_sessions_10"  to (perfectDays >= 10),
                "perfect_sessions_25"  to (perfectDays >= 25),
                "perfect_sessions_50"  to (perfectDays >= 50),
                "perfect_sessions_75"  to (perfectDays >= 75),
                "perfect_sessions_100" to (perfectDays >= 100),
                // Daily volume
                "productive_day"  to (maxDay >= 5),
                "power_day"       to (maxDay >= 10),
                "machine_day"     to (maxDay >= 15),
                "super_day"       to (maxDay >= 20),
                "marathon_day"    to (maxDay >= 25),
                // Speed
                "speed_run"       to speedCheck(3_600_000L),
                "lightning"       to speedCheck(1_800_000L),
                "blitz"           to speedCheck(900_000L),
                "flash"           to speedCheck(300_000L),
                "speed_run_5"     to (srDays >= 5),
                "speed_run_10"    to (srDays >= 10),
                // Time of day (all check today's session)
                "dawn_patrol"     to todayPriBefore(6),
                "morning_pro"     to todayPriBefore(8),
                "morning_rush"    to todayPriBefore(9),
                "early_bird"      to todayPriBefore(12),
                "lunch_legend"    to todayPriHour(12, 14),
                "golden_hour"     to todayPriHour(17, 19),
                "night_owl"       to todayPriAfter(21),
                "night_shift"     to todayPriAfter(22),
                "midnight_grind"  to midnightGrind,
                "early_bird_5"    to (ebDays >= 5),
                "early_bird_10"   to (ebDays >= 10),
                "night_owl_5"     to (noDays >= 5),
                "night_owl_10"    to (noDays >= 10),
                // Day of week
                "monday_momentum" to (allPriDoneToday && dayOfWeek == DayOfWeek.MONDAY),
                "hump_day"        to (allPriDoneToday && dayOfWeek == DayOfWeek.WEDNESDAY),
                "friday_finisher" to (allPriDoneToday && dayOfWeek == DayOfWeek.FRIDAY),
                "saturday_grind"  to (allPriDoneToday && dayOfWeek == DayOfWeek.SATURDAY),
                "sunday_prep"     to (allPriDoneToday && dayOfWeek == DayOfWeek.SUNDAY),
                "weekend_warrior" to weekendWarrior,
                // Comeback
                "comeback_kid"    to (comebackCount >= 1),
                "comeback_3"      to (comebackCount >= 3),
                "comeback_5"      to (comebackCount >= 5)
            )

            // ── Unlock loop ──────────────────────────────────────────────────────
            var firstNew: Achievement? = null
            for ((id, earned) in conditions) {
                if (earned && id !in alreadyUnlocked) {
                    val now = System.currentTimeMillis()
                    achievementDao.insert(AchievementEntity(id, now))
                    if (firstNew == null) {
                        firstNew = ACHIEVEMENT_DEFINITIONS.first { it.id == id }
                            .copy(isUnlocked = true, unlockedAt = now, progress = 1)
                    }
                }
            }

            // Refresh achievements list
            _achievements.value = loadAchievements(allTasks)

            // Signal UI overlay (only the first newly unlocked this check cycle)
            if (firstNew != null) {
                _newlyUnlockedAchievement.value = firstNew
                sendAchievementNotification(firstNew)
            }
        }
    }

    private fun sendAchievementNotification(achievement: Achievement) {
        val context = application
        val intent = Intent(context, MainActivity::class.java).apply {
            action = MainActivity.ACTION_OPEN_ACHIEVEMENTS
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, "achievement_unlocked")
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("Achievement Unlocked!")
            .setContentText(achievement.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${achievement.title}: ${achievement.description}"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(achievement.id.hashCode(), notification)
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
        val isCompletingNow = task.status != "COMPLETED"
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
            if (isCompletingNow) {
                try {
                    val mp = android.media.MediaPlayer.create(application, hr.fipu.organizationtool.R.raw.success)
                    mp?.apply {
                        start()
                        setOnCompletionListener { it.release() }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            ZenStackWidget().updateAll(application)
            val allTasks = repository.allTasks.first()
            checkAndUnlockAchievements(allTasks)
        }
    }

    fun addTaskToSession(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(Task(id = 0, title = title, isPriority = false))
            ZenStackWidget().updateAll(application)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            ZenStackWidget().updateAll(application)
        }
    }


    fun addScheduledTask(title: String, date: LocalDate) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val scheduledMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
            repository.insertTask(Task(id = 0, title = title, isPriority = false, scheduledFor = scheduledMillis))
            ZenStackWidget().updateAll(application)
            scheduleTaskReminder(title, scheduledMillis)
        }
    }

    private fun scheduleTaskReminder(title: String, scheduledMillis: Long) {
        val alarmManager = application.getSystemService(android.app.AlarmManager::class.java) ?: return
        // Notify at 8 AM on the scheduled day
        val zoneId = ZoneId.systemDefault()
        val scheduledDate = Instant.ofEpochMilli(scheduledMillis).atZone(zoneId).toLocalDate()
        val notifyAt = scheduledDate.atTime(8, 0).atZone(zoneId).toInstant().toEpochMilli()
        if (notifyAt <= System.currentTimeMillis()) return // don't schedule past notifications

        val intent = android.content.Intent(application, hr.fipu.organizationtool.service.TaskReminderReceiver::class.java).apply {
            putExtra(hr.fipu.organizationtool.service.TaskReminderReceiver.EXTRA_TASK_TITLE, title)
            putExtra(hr.fipu.organizationtool.service.TaskReminderReceiver.EXTRA_TASK_ID, title.hashCode())
        }
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            application, title.hashCode(), intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, notifyAt, pendingIntent)
    }

    fun selectDay(date: LocalDate) {
        _selectedDay.value = date
    }

    fun loadCompletionHistory() {
        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now()
            val historyMap = mutableMapOf<LocalDate, Int>()
            for (daysBack in 0L until 365L) {
                val day = today.minusDays(daysBack)
                val startOfDay = day.atStartOfDay(zoneId).toInstant().toEpochMilli()
                val endOfDay = day.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
                val tasks = repository.getTasksCompletedOn(startOfDay, endOfDay).first()
                historyMap[day] = tasks.size
            }
            _completionHistory.value = historyMap
        }
    }

    fun loadUnfinishedTasks() {
        viewModelScope.launch {
            val unfinished = repository.suggestedTasks.first()
            if (unfinished.isNotEmpty() && _brainDumpTasks.value.isEmpty()) {
                _brainDumpTasks.value = unfinished
            } else if (unfinished.isNotEmpty()) {
                val existingIds = _brainDumpTasks.value.map { it.id }.toSet()
                val newTasks = unfinished.filter { it.id !in existingIds }
                _brainDumpTasks.value = _brainDumpTasks.value + newTasks
            }
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
