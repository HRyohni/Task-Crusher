package hr.fipu.organizationtool

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import hr.fipu.organizationtool.ui.FoundationLab
import hr.fipu.organizationtool.ui.theme.ZenTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val ACTION_VIEW_TASKS = "hr.fipu.organizationtool.ACTION_VIEW_TASKS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == ACTION_VIEW_TASKS) {
            Log.d("ZenStack", "Deep link detected: ACTION_VIEW_TASKS")
        }

        setContent {
            ZenTheme {
                FoundationLab()
            }
        }
    }
}
