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
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextDecoration
import androidx.glance.unit.ColorProvider
import hr.fipu.organizationtool.MainActivity
import hr.fipu.organizationtool.data.AppDatabase
import hr.fipu.organizationtool.data.Task
import hr.fipu.organizationtool.ui.theme.ZenGrayDark
import hr.fipu.organizationtool.ui.theme.ZenIndigo
import hr.fipu.organizationtool.ui.theme.ZenSurface

import androidx.glance.appwidget.appWidgetBackgroundRadius
import android.content.Intent

class ZenStackWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dao = AppDatabase.getDatabase(context).taskDao()

        provideContent {
            val tasks by dao.getAllTasks().collectAsState(initial = emptyList())
            val priorityTasks = tasks.filter { it.isPriority }
            val otherTasks = tasks.filter { !it.isPriority }
            val completedCount = priorityTasks.count { it.status == "COMPLETED" }
            val progress = completedCount.toFloat() / priorityTasks.size.coerceAtLeast(1)

            val clickIntent = Intent(context, MainActivity::class.java).apply {
                action = MainActivity.ACTION_VIEW_TASKS
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(ZenSurface))
                    .appWidgetBackgroundRadius()
                    .padding(12.dp)
                    .clickable(actionStartActivity(clickIntent))
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {                    Text(
                        text = "ZenStack",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(ZenIndigo)
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "$completedCount/${priorityTasks.size}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(ZenGrayDark)
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = GlanceModifier.fillMaxWidth().height(8.dp),
                    color = ColorProvider(ZenIndigo),
                    backgroundColor = ColorProvider(Color.LightGray.copy(alpha = 0.2f))
                )
                
                Spacer(modifier = GlanceModifier.height(12.dp))
                
                Text(
                    text = "Priority",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ColorProvider(ZenGrayDark)
                    )
                )
                priorityTasks.forEach { task ->
                    TaskItem(task)
                }

                if (otherTasks.isNotEmpty()) {
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    Text(
                        text = "Brain Dump",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = ColorProvider(ZenGrayDark.copy(alpha = 0.6f))
                        )
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
                .padding(vertical = 4.dp)
                .clickable(actionRunCallback<ToggleTaskCallback>(
                    parameters = actionParametersOf(TaskActionHandler.TaskIdKey to task.id)
                )),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCompleted) "☑" else "☐",
                modifier = GlanceModifier.padding(end = 8.dp),
                style = TextStyle(
                    fontSize = if (isSmall) 14.sp else 18.sp,
                    color = ColorProvider(if (isCompleted) ZenIndigo else ZenGrayDark)
                )
            )
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
}
