package hr.fipu.organizationtool

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import hr.fipu.organizationtool.ui.ZenStackApp
import hr.fipu.organizationtool.ui.theme.ZenTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val ACTION_VIEW_TASKS = "hr.fipu.organizationtool.ACTION_VIEW_TASKS"
        const val ACTION_OPEN_ACHIEVEMENTS = "hr.fipu.organizationtool.ACTION_OPEN_ACHIEVEMENTS"
    }

    private val _openAchievementsTab = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == ACTION_VIEW_TASKS) {
            Log.d("ZenStack", "Deep link detected: ACTION_VIEW_TASKS")
        }

        val openAchievements = intent?.action == ACTION_OPEN_ACHIEVEMENTS
        _openAchievementsTab.value = openAchievements

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { /* result handled silently */ }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            ZenTheme {
                ZenStackApp(openAchievementsTab = _openAchievementsTab.value)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        _openAchievementsTab.value = intent.action == ACTION_OPEN_ACHIEVEMENTS
    }
}
