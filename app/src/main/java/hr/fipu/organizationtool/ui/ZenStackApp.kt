package hr.fipu.organizationtool.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.ui.theme.zenShadow
import hr.fipu.organizationtool.ui.theme.zenSpring
import hr.fipu.organizationtool.ui.components.BackTapOnboarding
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

enum class MainTab { TODAY, CALENDAR, ACHIEVEMENTS }

@Composable
fun ZenStackApp(viewModel: TaskViewModel = koinViewModel()) {
    val savedTasks by viewModel.savedTasks.collectAsState()
    
    var showSetup by remember { mutableStateOf(false) }
    
    // Determine if we should show setup or current tasks
    LaunchedEffect(savedTasks) {
        if (savedTasks != null && savedTasks!!.isEmpty()) {
            showSetup = true
        }
    }

    if (showSetup || savedTasks == null) {
        SetupFlow(viewModel) { showSetup = false }
    } else {
        MainShell(
            tasks = savedTasks!!,
            onRestart = { showSetup = true },
            onToggleComplete = { task -> viewModel.toggleTaskCompletion(task) }
        )
    }
}

@Composable
fun MainShell(
    tasks: List<Task>,
    onRestart: () -> Unit,
    onToggleComplete: (Task) -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.TODAY) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == MainTab.TODAY,
                    onClick = { selectedTab = MainTab.TODAY },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Today") },
                    label = { Text("Today") }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.CALENDAR,
                    onClick = { selectedTab = MainTab.CALENDAR },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                    label = { Text("Calendar") }
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.ACHIEVEMENTS,
                    onClick = { selectedTab = MainTab.ACHIEVEMENTS },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements") },
                    label = { Text("Achievements") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                MainTab.TODAY -> CurrentTasksView(
                    tasks = tasks,
                    onRestart = onRestart,
                    onToggleComplete = onToggleComplete
                )
                MainTab.CALENDAR -> CalendarPlaceholder()
                MainTab.ACHIEVEMENTS -> AchievementsPlaceholder()
            }
        }
    }
}

@Composable
fun CalendarPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Calendar — coming soon", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun AchievementsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Achievements — coming soon", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SetupFlow(viewModel: TaskViewModel, onFinished: () -> Unit) {
    var currentStep by remember { mutableStateOf(1) }
    val tasks by viewModel.brainDumpTasks.collectAsState()
    val suggestedTasks by viewModel.suggestedTasks.collectAsState()
    val selectedPriorityIds by viewModel.selectedPriorityIds.collectAsState()
    val hasSeenBackTapGuide by viewModel.hasSeenBackTapGuide.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (currentStep < 4) {
                Text(
                    text = "ZenStack Setup",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (currentStep) {
                1 -> BrainDumpStep(
                    tasks = tasks,
                    suggestedTasks = suggestedTasks,
                    onAddTask = { viewModel.addTask(it) },
                    onAddSuggestedTask = { viewModel.addSuggestedTask(it) },
                    onRemoveTask = { viewModel.removeTask(it) },
                    onNext = { if (tasks.isNotEmpty()) currentStep = 2 }
                )
                2 -> Power3Step(
                    tasks = tasks,
                    selectedIds = selectedPriorityIds,
                    onTogglePriority = { viewModel.togglePriority(it) },
                    onNext = { currentStep = 3 },
                    onBack = { currentStep = 1 }
                )
                3 -> ConfirmationStep(
                    tasks = tasks,
                    selectedIds = selectedPriorityIds,
                    onConfirm = {
                        viewModel.saveSession()
                        if (hasSeenBackTapGuide) {
                            onFinished()
                        } else {
                            currentStep = 4
                        }
                    },
                    onBack = { currentStep = 2 }
                )
                4 -> BackTapOnboarding(
                    onGotIt = {
                        viewModel.markBackTapGuideAsSeen()
                        onFinished()
                    }
                )
            }
        }
    }
}

@Composable
fun CurrentTasksView(
    tasks: List<Task>,
    onRestart: () -> Unit,
    onToggleComplete: (Task) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val priorityTasks = tasks.filter { it.isPriority }
    val otherTasks = tasks.filter { !it.isPriority }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onRestart) {
                Icon(Icons.Default.Refresh, contentDescription = "New Session")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Focus Mode", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Tasks Done: ${priorityTasks.count { it.status == "COMPLETED" }}/${priorityTasks.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item { Text("Top 3 Priorities", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
            
            items(priorityTasks) { task ->
                TaskCard(task, isPriority = true) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggleComplete(task)
                }
            }

            if (otherTasks.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item { Text("Brain Dump", style = MaterialTheme.typography.titleMedium) }
                items(otherTasks) { task ->
                    TaskCard(task, isPriority = false) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleComplete(task)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, isPriority: Boolean, onClick: () -> Unit) {
    val isCompleted = task.status == "COMPLETED"
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isPriority) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isPriority) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isCompleted, onCheckedChange = { onClick() })
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
        }
    }
}

@Composable
fun BrainDumpStep(
    tasks: List<Task>,
    suggestedTasks: List<Task>,
    onAddTask: (String) -> Unit,
    onAddSuggestedTask: (Task) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onNext: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Brain Dump", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Get everything out of your head.", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineSmall,
            placeholder = { Text("What's on your mind?", style = MaterialTheme.typography.headlineSmall) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            trailingIcon = {
                IconButton(onClick = {
                    if (text.isNotBlank()) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAddTask(text)
                        text = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Suggested Tasks
        val filteredSuggestions = suggestedTasks.filter { sugg -> tasks.none { it.id == sugg.id } }
        if (filteredSuggestions.isNotEmpty()) {
            Text("Suggested for you", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(filteredSuggestions) { task ->
                    SuggestionChip(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onAddSuggestedTask(task)
                        },
                        label = { Text(task.title) },
                        icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                            Icon(Icons.Default.Close, contentDescription = "Remove")
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
                    Text("Next: Summary")
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

@Composable
fun ConfirmationStep(
    tasks: List<Task>,
    selectedIds: Set<Int>,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val priorityTasks = tasks.filter { selectedIds.contains(it.id) }
    val otherTasks = tasks.filter { !selectedIds.contains(it.id) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Step 3: Confirmation", style = MaterialTheme.typography.titleMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        Text("Top Priorities", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        priorityTasks.forEach { task ->
            Text("• ${task.title}", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Brain Dump", fontWeight = FontWeight.Bold)
        otherTasks.forEach { task ->
            Text("• ${task.title}", modifier = Modifier.padding(start = 8.dp, top = 4.dp), color = Color.Gray)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Back")
            }
            Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                Text("Set Focus")
            }
        }
    }
}
