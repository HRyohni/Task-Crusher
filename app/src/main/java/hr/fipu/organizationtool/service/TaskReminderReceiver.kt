package hr.fipu.organizationtool.service

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hr.fipu.organizationtool.MainActivity

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: return
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, 0)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, taskId, openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, "scheduled_task")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Task Reminder")
            .setContentText(taskTitle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(taskId, notification)
        }
    }

    companion object {
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_TASK_ID = "task_id"
    }
}
