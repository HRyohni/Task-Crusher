package hr.fipu.organizationtool

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import hr.fipu.organizationtool.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ZenStackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ZenStackApplication)
            modules(appModule)
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val achievementChannel = NotificationChannel(
                "achievement_unlocked",
                "Achievement Unlocked",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when a new achievement is unlocked"
            }
            val scheduledChannel = NotificationChannel(
                "scheduled_task",
                "Scheduled Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you about tasks scheduled for today"
            }
            manager.createNotificationChannel(achievementChannel)
            manager.createNotificationChannel(scheduledChannel)
        }
    }
}
