package hr.fipu.organizationtool.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/**
 * Zen Design System Physics (D-04)
 * 
 * Focus on tactility through medium-low stiffness and soft damping.
 * This provides a deliberate, high-quality feel to transitions.
 */

// Zen Spring Constants
const val ZenStiffness = Spring.StiffnessMediumLow // 400f
const val ZenDamping = 0.75f // Slightly soft bounce

/**
 * Standard spring helper for Zen transitions.
 */
fun <T> zenSpring(
    dampingRatio: Float = ZenDamping,
    stiffness: Float = ZenStiffness,
    visibilityThreshold: T? = null
) = spring<T>(
    dampingRatio = dampingRatio,
    stiffness = stiffness,
    visibilityThreshold = visibilityThreshold
)
