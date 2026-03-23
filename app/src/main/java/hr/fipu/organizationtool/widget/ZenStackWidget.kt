package hr.fipu.organizationtool.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextDecoration
import hr.fipu.organizationtool.data.AppDatabase
import hr.fipu.organizationtool.data.Task

class ZenStackWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).taskDao()
        
        provideContent {
            val tasks by dao.getAllTasks().collectAsState(initial = emptyList())
            val priorityTasks = tasks.filter { it.isPriority }
            val otherTasks = tasks.filter { !it.isPriority }
            val completedCount = priorityTasks.count { it.status == "COMPLETED" }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ImageProvider(android.R.drawable.dialog_holo_light_frame))
                    .padding(8.dp)
            ) {
                Text(
                    text = "ZenStack - Done: $completedCount/${priorityTasks.size}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = GlanceModifier.height(8.dp))
                
                Text(
                    text = "Priority",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                priorityTasks.forEach { task ->
                    TaskItem(task)
                }

                if (otherTasks.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Text(
                        text = "Brain Dump",
                        style = TextStyle(fontSize = 10.sp)
                    )
                    otherTasks.take(3).forEach { task ->
                        TaskItem(task, isSmall = true)
                    }
                }
            }
        }
    }

    @Composable
    private fun TaskItem(task: Task, isSmall: Boolean = false) {
        val isCompleted = task.status == "COMPLETED"
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clickable(actionRunCallback<ToggleTaskCallback>(
                    parameters = actionParametersOf(TaskActionHandler.TaskIdKey to task.id)
                )),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCompleted) "☑" else "☐",
                modifier = GlanceModifier.padding(end = 4.dp),
                style = TextStyle(fontSize = if (isSmall) 12.sp else 16.sp)
            )
            Text(
                text = task.title,
                style = TextStyle(
                    fontSize = if (isSmall) 10.sp else 14.sp,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
            )
        }
    }
}
