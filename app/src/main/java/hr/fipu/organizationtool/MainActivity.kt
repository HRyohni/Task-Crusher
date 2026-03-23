package hr.fipu.organizationtool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import hr.fipu.organizationtool.ui.FoundationLab
import hr.fipu.organizationtool.ui.theme.ZenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZenTheme {
                FoundationLab()
            }
        }
    }
}
