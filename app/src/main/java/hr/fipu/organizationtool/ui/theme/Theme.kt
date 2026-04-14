package hr.fipu.organizationtool.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ZenDarkColorScheme = darkColorScheme(
    primary = ZenOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3D2800),
    onPrimaryContainer = ZenOrangeLight,
    secondary = ZenSlate,
    onSecondary = Color.White,
    tertiary = ZenGrayMedium,
    onTertiary = Color.White,
    error = ZenError,
    onError = ZenOnError,
    surface = Color(0xFF1A1A1A),
    background = Color(0xFF121212),
    onSurface = ZenWhite,
    onBackground = ZenWhite,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFCCCCCC)
)

private val ZenLightColorScheme = lightColorScheme(
    primary = ZenOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF0DC),
    onPrimaryContainer = Color(0xFF3D2800),
    secondary = ZenSlate,
    onSecondary = Color.White,
    tertiary = ZenGrayMedium,
    onTertiary = Color.White,
    error = ZenError,
    onError = ZenOnError,
    surface = ZenSurface,
    background = ZenBackground,
    onSurface = ZenGrayDark,
    onBackground = ZenGrayDark,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF444444)
)

@Composable
fun ZenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ZenDarkColorScheme else ZenLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZenTypography,
        content = content
    )
}
