package hr.fipu.organizationtool.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hr.fipu.organizationtool.data.Task
import org.koin.androidx.compose.koinViewModel

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
        CurrentTasksView(
            tasks = savedTasks!!,
            onRestart = { showSetup = true },
            onToggleComplete = { task ->
                viewModel.toggleTaskCompletion(task)
            }
        )
    }
}

@Composable
fun SetupFlow(viewModel: TaskViewModel, onFinished: () -> Unit) {
    var currentStep by remember { mutableStateOf(1) }
    val tasks by viewModel.brainDumpTasks.collectAsState()
    val selectedPriorityIds by viewModel.selectedPriorityIds.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "ZenStack Setup",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            when (currentStep) {
                1 -> BrainDumpStep(
                    tasks = tasks,
                    onAddTask = { viewModel.addTask(it) },
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
                        onFinished()
                    },
                    onBack = { currentStep = 2 }
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
    var showHowTo by remember { mutableStateOf(false) }

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

            item {
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = { showHowTo = !showHowTo }) {
                    Text("How to use Back Tap for quick access?")
                }
                AnimatedVisibility(visible = showHowTo) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("1. Open System Settings", fontWeight = FontWeight.Bold)
                            Text("2. Search for 'Quick Tap' or 'Back Tap'")
                            Text("3. Select 'Open App'")
                            Text("4. Choose 'ZenStack'")
                            Text("Now double-tap your phone's back to focus instantly!")
                        }
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
    onAddTask: (String) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onNext: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Step 1: Brain Dump", style = MaterialTheme.typography.titleMedium)
        Text("Get everything out of your head.", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("What's on your mind?") },
            trailingIcon = {
                IconButton(onClick = {
                    if (text.isNotBlank()) {
                        onAddTask(text)
                        text = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                Surface(
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(task.title, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onRemoveTask(task) }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove")
                        }
                    }
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = tasks.isNotEmpty()
        ) {
            Text("Next: Prioritize")
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
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Step 2: The Power 3", style = MaterialTheme.typography.titleMedium)
        Text("Select your 3 most important tasks.", style = MaterialTheme.typography.bodySmall)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks) { task ->
                val isSelected = selectedIds.contains(task.id)
                val isDimmed = selectedIds.size >= 3 && !isSelected

                Surface(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { onTogglePriority(task) }
                        .alpha(if (isDimmed) 0.5f else 1f),
                    shape = RoundedCornerShape(8.dp),
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
