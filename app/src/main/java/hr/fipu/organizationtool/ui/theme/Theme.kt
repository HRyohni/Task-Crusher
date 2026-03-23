package hr.fipu.organizationtool.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ZenDarkColorScheme = darkColorScheme(
    primary = ZenPrimary,
    onPrimary = ZenOnPrimary,
    secondary = ZenSecondary,
    onSecondary = ZenOnSecondary,
    tertiary = ZenTertiary,
    onTertiary = ZenOnTertiary,
    error = ZenError,
    onError = ZenOnError,
    surface = ZenGrayDark,
    background = ZenGrayDark,
    onSurface = ZenWhite,
    onBackground = ZenWhite
)

private val ZenLightColorScheme = lightColorScheme(
    primary = ZenPrimary,
    onPrimary = ZenOnPrimary,
    secondary = ZenSecondary,
    onSecondary = ZenOnSecondary,
    tertiary = ZenTertiary,
    onTertiary = ZenOnTertiary,
    error = ZenError,
    onError = ZenOnError,
    surface = ZenSurface,
    background = ZenBackground,
    onSurface = ZenGrayDark,
    onBackground = ZenGrayDark
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
