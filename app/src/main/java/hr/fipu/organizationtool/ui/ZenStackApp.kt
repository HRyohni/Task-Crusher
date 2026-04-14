package hr.fipu.organizationtool.ui

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.ui.theme.ZenCardPurple
import hr.fipu.organizationtool.ui.theme.ZenDeepPurple
import hr.fipu.organizationtool.ui.theme.ZenIndigo
import hr.fipu.organizationtool.ui.theme.zenShadow
import hr.fipu.organizationtool.ui.theme.zenSpring

import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

enum class MainTab { TODAY, CALENDAR, ACHIEVEMENTS }

@Composable
fun ZenStackApp(viewModel: TaskViewModel = koinViewModel(), openAchievementsTab: Boolean = false) {
    val savedTasks by viewModel.savedTasks.collectAsState()

    var showSetup by remember { mutableStateOf(false) }

    // Determine if we should show setup or current tasks
    LaunchedEffect(savedTasks) {
        if (savedTasks != null && savedTasks!!.isEmpty()) {
            showSetup = true
        }
    }

    val completionHistory by viewModel.completionHistory.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val tasksForSelectedDay by viewModel.tasksForSelectedDay.collectAsState()
    val scheduledTasksForSelectedDay by viewModel.scheduledTasksForSelectedDay.collectAsState()
    val newlyUnlockedAchievement by viewModel.newlyUnlockedAchievement.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    LaunchedEffect(newlyUnlockedAchievement) {
        if (newlyUnlockedAchievement != null) {
            delay(4000)
            viewModel.dismissAchievementUnlock()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showSetup || savedTasks == null) {
            SetupFlow(viewModel, onFinished = { showSetup = false }, onBack = { showSetup = false })
        } else {
            MainShell(
                tasks = savedTasks!!,
                onRestart = { showSetup = true },
                onToggleComplete = { task -> viewModel.toggleTaskCompletion(task) },
                onDeleteTask = { task -> viewModel.deleteTask(task) },
                onAddTask = { title -> viewModel.addTaskToSession(title) },
                completionHistory = completionHistory,
                selectedDay = selectedDay,
                tasksForSelectedDay = tasksForSelectedDay,
                scheduledTasksForSelectedDay = scheduledTasksForSelectedDay,
                onDaySelected = { date -> viewModel.selectDay(date) },
                onAddScheduledTask = { title, date -> viewModel.addScheduledTask(title, date) },
                achievements = achievements,
                initialTab = if (openAchievementsTab) MainTab.ACHIEVEMENTS else MainTab.TODAY
            )
        }

        AnimatedVisibility(
            visible = newlyUnlockedAchievement != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            newlyUnlockedAchievement?.let { achievement ->
                AchievementUnlockBanner(
                    achievement = achievement,
                    onDismiss = { viewModel.dismissAchievementUnlock() }
                )
            }
        }
    }
}

@Composable
fun MainShell(
    tasks: List<Task>,
    onRestart: () -> Unit,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onAddTask: (String) -> Unit,
    completionHistory: Map<LocalDate, Int>,
    selectedDay: LocalDate?,
    tasksForSelectedDay: List<Task>,
    scheduledTasksForSelectedDay: List<Task>,
    onDaySelected: (LocalDate) -> Unit,
    onAddScheduledTask: (String, LocalDate) -> Unit,
    achievements: List<Achievement>,
    initialTab: MainTab = MainTab.TODAY
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabs = MainTab.entries
    var dragAccum by remember { mutableStateOf(0f) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = ZenDeepPurple,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = selectedTab == MainTab.TODAY,
                    onClick = { selectedTab = MainTab.TODAY },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Today") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZenIndigo,
                        selectedTextColor = ZenIndigo,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = ZenIndigo.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.CALENDAR,
                    onClick = { selectedTab = MainTab.CALENDAR },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZenIndigo,
                        selectedTextColor = ZenIndigo,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = ZenIndigo.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.ACHIEVEMENTS,
                    onClick = { selectedTab = MainTab.ACHIEVEMENTS },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ZenIndigo,
                        selectedTextColor = ZenIndigo,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = ZenIndigo.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(selectedTab) {
                    detectHorizontalDragGestures(
                        onDragEnd = { dragAccum = 0f },
                        onDragCancel = { dragAccum = 0f },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            dragAccum += dragAmount
                            if (dragAccum < -80f) {
                                val idx = tabs.indexOf(selectedTab)
                                if (idx < tabs.lastIndex) selectedTab = tabs[idx + 1]
                                dragAccum = 0f
                            } else if (dragAccum > 80f) {
                                val idx = tabs.indexOf(selectedTab)
                                if (idx > 0) selectedTab = tabs[idx - 1]
                                dragAccum = 0f
                            }
                        }
                    )
                }
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val direction = if (tabs.indexOf(targetState) > tabs.indexOf(initialState)) 1 else -1
                    (slideInHorizontally(tween(300)) { it * direction } + fadeIn(tween(200))) togetherWith
                    (slideOutHorizontally(tween(300)) { -it * direction } + fadeOut(tween(200)))
                },
                label = "TabTransition"
            ) { tab ->
                when (tab) {
                    MainTab.TODAY -> CurrentTasksView(
                        tasks = tasks,
                        onRestart = onRestart,
                        onToggleComplete = onToggleComplete,
                        onDeleteTask = onDeleteTask,
                        onAddTask = onAddTask
                    )
                    MainTab.CALENDAR -> CalendarScreen(
                        completionHistory = completionHistory,
                        selectedDay = selectedDay,
                        tasksForSelectedDay = tasksForSelectedDay,
                        scheduledTasksForSelectedDay = scheduledTasksForSelectedDay,
                        onDaySelected = onDaySelected,
                        onAddScheduledTask = onAddScheduledTask,
                        modifier = Modifier.fillMaxSize()
                    )
                    MainTab.ACHIEVEMENTS -> AchievementsScreen(
                        achievements = achievements,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementUnlockBanner(achievement: Achievement, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDismiss() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Achievement Unlocked!",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SetupFlow(viewModel: TaskViewModel, onFinished: () -> Unit, onBack: () -> Unit) {
    var currentStep by remember { mutableStateOf(1) }
    val tasks by viewModel.brainDumpTasks.collectAsState()
    val selectedPriorityIds by viewModel.selectedPriorityIds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUnfinishedTasks()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            when (currentStep) {
                1 -> BrainDumpStep(
                    tasks = tasks,
                    onAddTask = { viewModel.addTask(it) },
                    onRemoveTask = { viewModel.removeTask(it) },
                    onNext = { if (tasks.isNotEmpty()) currentStep = 2 },
                    onBack = onBack
                )
                2 -> Power3Step(
                    tasks = tasks,
                    selectedIds = selectedPriorityIds,
                    onTogglePriority = { viewModel.togglePriority(it) },
                    onNext = {
                        viewModel.saveSession()
                        onFinished()
                    },
                    onBack = { currentStep = 1 }
                )
            }
        }
    }
}

@Composable
fun CurrentTasksView(
    tasks: List<Task>,
    onRestart: () -> Unit,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onAddTask: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var newTaskText by remember { mutableStateOf("") }

    val submitNewTask: () -> Unit = {
        if (newTaskText.isNotBlank()) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onAddTask(newTaskText)
            newTaskText = ""
        }
    }

    val today = LocalDate.now()
    val zoneId = ZoneId.systemDefault()

    fun Task.completedToday(): Boolean {
        val at = completedAt ?: return false
        return Instant.ofEpochMilli(at).atZone(zoneId).toLocalDate() == today
    }

    val activePriority = tasks.filter { it.isPriority && (it.status != "COMPLETED" || it.completedToday()) }
    val activeBrainDump = tasks.filter { !it.isPriority && (it.status != "COMPLETED" || it.completedToday()) }

    val allPriorityDone = activePriority.isNotEmpty() && activePriority.all { it.status == "COMPLETED" }
    var confettiShown by remember(tasks.map { it.id }.toSet()) { mutableStateOf(false) }
    val showConfetti = allPriorityDone && !confettiShown

    Box(modifier = Modifier.fillMaxSize().background(ZenDeepPurple)) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onRestart,
                    containerColor = ZenCardPurple,
                    contentColor = Color.White
                ) {
                    Text("💸", style = MaterialTheme.typography.headlineMedium)
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        "Focus Mode 💰",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "${activePriority.count { it.status == "COMPLETED" }} / ${activePriority.size} important stuff done",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(12.dp))
                }




                if (activePriority.isEmpty() && activeBrainDump.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(bottom = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Great job! Don't stop now.\nAdd tasks with 💸",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(activePriority) { task ->
                    TaskCard(task, isPriority = true,
                        onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onToggleComplete(task) },
                        onDelete = { onDeleteTask(task) })
                }

                if (activeBrainDump.isNotEmpty()) {
                    item {
                        Text(
                            "BRAIN DUMP",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.5f),
                            letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp),
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(activeBrainDump) { task ->
                        TaskCard(task, isPriority = false,
                            onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onToggleComplete(task) },
                            onDelete = { onDeleteTask(task) })
                    }
                }

            }
        }

        if (showConfetti) {
            val parties = listOf(
                Party(
                    emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(100),
                    position = Position.Relative(0.5, 0.0)
                )
            )
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = parties,
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        if (activeSystems == 0) confettiShown = true
                    }
                }
            )
        }
    }
}

@Composable
private fun CircleCheckbox(checked: Boolean, onClick: () -> Unit, size: Dp = 36.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (checked) ZenIndigo else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}

@Composable
fun TaskCard(task: Task, isPriority: Boolean, onClick: () -> Unit, onDelete: (() -> Unit)? = null) {
    val isCompleted = task.status == "COMPLETED"
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = if (isPriority) 72.dp else 0.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = ZenCardPurple
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = if (isPriority) 14.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleCheckbox(
                checked = isCompleted,
                onClick = onClick,
                size = if (isPriority) 36.dp else 32.dp
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = if (isPriority) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
                fontWeight = if (isPriority) FontWeight.Bold else FontWeight.Medium,
                color = if (isCompleted) Color.White.copy(alpha = 0.45f) else Color.White,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
            if (onDelete != null && !isCompleted) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete task",
                        tint = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
fun BrainDumpStep(
    tasks: List<Task>,
    onAddTask: (String) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    val submitTask: () -> Unit = {
        if (text.isNotBlank()) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onAddTask(text)
            text = ""
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) {
            Text("← Brain Dump", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        Text("Get everything out of your head. I mean EVERYTHING.", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineSmall,
            placeholder = { Text("What's there to do?", style = MaterialTheme.typography.headlineSmall) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submitTask() }),
            trailingIcon = {
                IconButton(onClick = { submitTask() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                Surface(
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            task.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(onClick = { onRemoveTask(task) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = tasks.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text("Next: Prioritize", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun Power3Step(
    tasks: List<Task>,
    selectedIds: Set<Int>,
    onTogglePriority: (Task) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showWarning by remember { mutableStateOf(false) }

    // Logic to show warning and double-vibrate if 4th item attempted
    val handleToggle: (Task) -> Unit = { task ->
        if (selectedIds.size >= 3 && !selectedIds.contains(task.id)) {
            showWarning = true
            // Double vibration effect
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            // Note: In a real app we'd use a Coroutine for the second tap after delay
            // But for this UI component we'll handle the visual warning here
        } else {
            onTogglePriority(task)
        }
    }

    // Effect to hide warning after 2 seconds
    LaunchedEffect(showWarning) {
        if (showWarning) {
            // Trigger second vibration for "double-vibration" effect
            delay(100)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            
            delay(2000)
            showWarning = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Step 2: The Power 3", style = MaterialTheme.typography.titleMedium)
            Text("Select your 3 most important tasks.", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tasks, key = { it.id }) { task ->
                    val isSelected = selectedIds.contains(task.id)
                    val isDimmed = selectedIds.size >= 3 && !isSelected

                    // Pop motion animation
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1f,
                        animationSpec = zenSpring(),
                        label = "PopScale"
                    )

                    Surface(
                        modifier = Modifier
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                            .scale(scale)
                            .zenShadow(
                                elevation = if (isSelected) 8.dp else 0.dp,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { handleToggle(task) }
                            .alpha(if (isDimmed) 0.5f else 1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            task.title,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back")
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    enabled = selectedIds.isNotEmpty()
                ) {
                    Text("Set Focus")
                }
            }
        }

        // Pill Warning Popup
        AnimatedVisibility(
            visible = showWarning,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.zenShadow(4.dp, RoundedCornerShape(24.dp))
            ) {
                Text(
                    "Select only 3 priorities.",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

