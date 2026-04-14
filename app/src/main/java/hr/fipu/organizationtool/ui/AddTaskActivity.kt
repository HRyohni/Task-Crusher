package hr.fipu.organizationtool.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import hr.fipu.organizationtool.data.AppDatabase
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.data.TaskRepository
import hr.fipu.organizationtool.ui.theme.ZenTheme
import hr.fipu.organizationtool.widget.ZenStackWidget
import kotlinx.coroutines.launch

class AddTaskActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        setContent {
            ZenTheme {
                val focusRequester = remember { FocusRequester() }
                var text by remember { mutableStateOf("") }
                var addedCount by remember { mutableIntStateOf(0) }

                val addTask: () -> Unit = {
                    if (text.isNotBlank()) {
                        val title = text
                        text = ""
                        addedCount++
                        lifecycleScope.launch {
                            val repository = TaskRepository(AppDatabase.getDatabase(this@AddTaskActivity).taskDao())
                            repository.insertTask(Task(id = 0, title = title, isPriority = false))
                            ZenStackWidget().updateAll(this@AddTaskActivity)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                        .clickable { finish() },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clickable(enabled = false) {},
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Add Tasks",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (addedCount > 0) {
                                Text(
                                    "$addedCount task${if (addedCount > 1) "s" else ""} added",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = text,
                                onValueChange = { text = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                placeholder = { Text("What needs to be done?") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { addTask() })
                            )
                            Spacer(Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { finish() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Done")
                                }
                                Button(
                                    onClick = { addTask() },
                                    modifier = Modifier.weight(1f),
                                    enabled = text.isNotBlank()
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }

}
