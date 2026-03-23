package hr.fipu.organizationtool.widget

import android.content.Context
import android.media.MediaPlayer
import androidx.glance.GlanceId
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import androidx.glance.action.ActionParameters
import hr.fipu.organizationtool.data.AppDatabase
import hr.fipu.organizationtool.R

class TaskActionHandler : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[TaskIdKey] ?: return
        val dao = AppDatabase.getDatabase(context).taskDao()
        
        // Find the task and toggle its completion
        // This is a bit simplified; in a real app, you'd fetch it from the DB
        // For now, we'll assume we can update it directly if we had the full object
        // Since we only have ID, we might need a query
    }

    companion object {
        val TaskIdKey = ActionParameters.Key<Int>("taskId")
    }
}

class ToggleTaskCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val taskId = parameters[TaskActionHandler.TaskIdKey] ?: return
        val dao = AppDatabase.getDatabase(context).taskDao()
        
        // Use a background thread/scope if needed, but ActionCallback is already suspend
        val tasks = dao.getAllTasksOnce() // Need to add this helper to DAO
        val task = tasks.find { it.id == taskId }
        task?.let {
            val nextStatus = if (it.status == "COMPLETED") "TODO" else "COMPLETED"
            val nextCompletedAt = if (nextStatus == "COMPLETED") System.currentTimeMillis() else null
            val updatedTask = it.copy(status = nextStatus, completedAt = nextCompletedAt)
            dao.updateTask(updatedTask)

            if (updatedTask.status == "COMPLETED") {
                playSuccessSound(context)
            }
        }
        
        // Update the widget UI
        ZenStackWidget().updateAll(context)
    }

    private fun playSuccessSound(context: Context) {
        try {
            val mp = MediaPlayer.create(context, hr.fipu.organizationtool.R.raw.zen_success)
            mp?.apply {
                start()
                setOnCompletionListener { it.release() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
