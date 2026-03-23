package hr.fipu.organizationtool.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom modifier for "Physical Shadows" to emphasize the tactile nature of ZenStack elements.
 * Uses a soft blur and subtle offset instead of standard Material 3 tonal elevation.
 */
fun Modifier.zenShadow(
    elevation: Dp = 4.dp,
    shape: Shape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    clip = clip,
    ambientColor = ambientColor,
    spotColor = spotColor
)
