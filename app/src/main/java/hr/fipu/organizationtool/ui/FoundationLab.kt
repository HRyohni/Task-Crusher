package hr.fipu.organizationtool.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.data.TaskRepository
import hr.fipu.organizationtool.ui.theme.zenShadow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundationLab(repository: TaskRepository = koinInject()) {
    val tasks by repository.allTasks.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foundation Lab") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Theme verification
            Text(
                "Grayscale Palette Check",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            // Shadow verification
            Card(
                modifier = Modifier
                    .size(240.dp, 120.dp)
                    .zenShadow(elevation = 12.dp, shape = RectangleShape),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tactile Shadow (Zen)", style = MaterialTheme.typography.labelLarge)
                }
            }

            HorizontalDivider()

            // Database & DI verification
            Text("Repository Data Check", style = MaterialTheme.typography.titleMedium)
            
            if (tasks.isEmpty()) {
                Text("No tasks found. Tap to add test task.", style = MaterialTheme.typography.bodySmall)
            } else {
                tasks.take(3).forEach { task ->
                    Text("Task: ${task.title} (Status: ${task.status})")
                }
            }

            Button(onClick = {
                scope.launch {
                    repository.insertTask(
                        Task(title = "Test Foundation Task ${tasks.size + 1}")
                    )
                }
            }) {
                Text("Add Test Task")
            }
            
            Button(
                onClick = { scope.launch { repository.deleteAllTasks() } },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Clear All Data")
            }
        }
    }
}
