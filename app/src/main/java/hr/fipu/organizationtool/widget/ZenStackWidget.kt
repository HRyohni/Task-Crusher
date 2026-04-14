package hr.fipu.organizationtool.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import android.content.Intent
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import hr.fipu.organizationtool.ui.AddTaskActivity
import hr.fipu.organizationtool.MainActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextDecoration
import androidx.glance.unit.ColorProvider
import hr.fipu.organizationtool.data.AppDatabase
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.data.TaskRepository
import hr.fipu.organizationtool.ui.theme.ZenGrayDark
import hr.fipu.organizationtool.ui.theme.ZenIndigo
import hr.fipu.organizationtool.ui.theme.ZenSurface

class ZenStackWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = TaskRepository(AppDatabase.getDatabase(context).taskDao())

        val widgetTitles = listOf(
            "Success Farmer",
            "Money Generator",
            "Goal Crusher",
            "Dream Builder",
            "Focus Machine",
            "Task Slayer",
            "Grind Mode",
            "Level Up",
            "Boss Moves",
            "Win Factory",
            "Aura Farming",
            "Unstoppable Machine"
        )

        provideContent {
            val priorityTasks by repository.priorityTasks.collectAsState(initial = emptyList())
            val otherTasks by repository.allNonPriorityTasks.collectAsState(initial = emptyList())
            val completedCount = priorityTasks.count { it.status == "COMPLETED" }
            val widgetTitle = widgetTitles[(System.currentTimeMillis() / 60000 % widgetTitles.size).toInt()]
            val progress = completedCount.toFloat() / priorityTasks.size.coerceAtLeast(1)

            val activeBrainDumpTasks = otherTasks.filter { it.status != "COMPLETED" }
            val isAllComplete = priorityTasks.isNotEmpty() && completedCount == priorityTasks.size

            LazyColumn(modifier = GlanceModifier.fillMaxSize().background(ColorProvider(ZenSurface)).padding(12.dp).cornerRadius(16.dp)) {
                // Header row: app name + completion count
                item {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = widgetTitle,
                            modifier = GlanceModifier.clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ColorProvider(ZenIndigo))
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = "$completedCount/${priorityTasks.size}",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorProvider(ZenGrayDark))
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Box(
                            modifier = GlanceModifier
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                                .cornerRadius(8.dp)
                                .background(ColorProvider(ZenIndigo.copy(alpha = 0.35f)))
                                .clickable(
                                    actionStartActivity(
                                        Intent(context, AddTaskActivity::class.java)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorProvider(ZenIndigo))
                            )
                        }
                    }
                }
                // Progress bar
                item {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = GlanceModifier.fillMaxWidth().height(8.dp).padding(bottom = 12.dp),
                        color = ColorProvider(ZenIndigo),
                        backgroundColor = ColorProvider(Color.LightGray.copy(alpha = 0.2f))
                    )
                }
                // Celebration banner — shown only when all 3 priorities are complete
                if (isAllComplete) {
                    item {
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(ColorProvider(ZenIndigo.copy(alpha = 0.12f)))
                                .cornerRadius(3.dp)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "All done! Great work.",
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(ZenIndigo)
                                )
                            )
                        }
                    }
                }
                // Priority section header
                item {
                    Text(
                        text = "Priority",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ColorProvider(ZenGrayDark)),
                        modifier = GlanceModifier.padding(top = 2.dp, bottom = 4.dp)
                    )
                }
                // Priority task rows — no cap, all tasks
                items(priorityTasks) { task ->
                    TaskItem(task)
                }
                // Brain dump section header (only shown when active non-priority tasks exist)
                if (activeBrainDumpTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Brain Dump",
                            style = TextStyle(fontSize = 11.sp, color = ColorProvider(ZenGrayDark.copy(alpha = 0.6f))),
                            modifier = GlanceModifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    // Brain dump task rows — active (non-completed) only
                    items(activeBrainDumpTasks) { task ->
                        TaskItem(task, isSmall = true)
                    }
                }
            }
        }
    }

    @Composable
    private fun TaskItem(task: Task, isSmall: Boolean = false) {
        val isCompleted = task.status == "COMPLETED"
        if (!isSmall) {
            Box(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(ZenIndigo.copy(alpha = 0.18f)))
                        .cornerRadius(8.dp)
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .clickable(actionRunCallback<ToggleTaskCallback>(
                            parameters = actionParametersOf(TaskActionHandler.TaskIdKey to task.id)
                        )),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskItemContent(task, isSmall = false, isCompleted = isCompleted)
                }
            }
        } else {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp, horizontal = 2.dp)
                    .clickable(actionRunCallback<ToggleTaskCallback>(
                        parameters = actionParametersOf(TaskActionHandler.TaskIdKey to task.id)
                    )),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskItemContent(task, isSmall = true, isCompleted = isCompleted)
            }
        }
    }

    @Composable
    private fun TaskItemContent(task: Task, isSmall: Boolean, isCompleted: Boolean) {
        val circleSize = if (isSmall) 18.dp else 22.dp
        Box(
            modifier = GlanceModifier
                .size(circleSize)
                .cornerRadius(circleSize / 2)
                .background(ColorProvider(if (isCompleted) ZenIndigo else Color.White)),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Text(
                    text = "✓",
                    style = TextStyle(
                        fontSize = if (isSmall) 10.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color.White)
                    )
                )
            }
        }
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = task.title,
            style = TextStyle(
                fontSize = if (isSmall) 12.sp else 14.sp,
                color = ColorProvider(if (isCompleted) ZenGrayDark.copy(alpha = 0.5f) else ZenGrayDark),
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
        )
    }
}

class ZenStackWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ZenStackWidget()
}
