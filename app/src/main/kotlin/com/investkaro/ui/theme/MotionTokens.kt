package com.investkaro.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

/**
 * Shared motion tokens for Invest Karo.
 * Provides coherent durations, standard easing curves, and system reduced-motion detection.
 */
object MotionTokens {
    const val Fast: Int = 150
    const val Standard: Int = 220
    const val Emphasis: Int = 320
    const val CountSettled: Int = 450
    const val DebounceMillis: Long = 300L

    val EasingStandard: Easing = FastOutSlowInEasing
    val EasingDecelerate: Easing = LinearOutSlowInEasing
    val EasingEmphasized: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    /**
     * Spring-based counter motion (duration ~350-500ms, damping: 25-30, stiffness: 200-250)
     */
    val SpringCounter = spring<Float>(
        dampingRatio = 0.95f,
        stiffness = 220f
    )

    /**
     * Spring-based pill indicator layout transition
     */
    val SpringPill = spring<Float>(
        dampingRatio = 0.85f,
        stiffness = 220f
    )

    fun <T> fastTween() = tween<T>(durationMillis = Fast, easing = EasingStandard)
    fun <T> standardTween() = tween<T>(durationMillis = Standard, easing = EasingStandard)
    fun <T> emphasisTween() = tween<T>(durationMillis = Emphasis, easing = EasingEmphasized)
}

/**
 * Checks system animator duration scale to respect user's reduced-motion preference.
 */
@Composable
fun isReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            scale == 0f
        } catch (_: Exception) {
            false
        }
    }
}

/**
 * Reusable press feedback modifier providing subtle tactile scale (~0.97)
 * on touch down and smooth release (140ms), preserving accessibility and touch targets.
 */
fun Modifier.pressScaleEffect(
    pressedScale: Float = 0.97f,
    interactionSource: MutableInteractionSource? = null
): Modifier = composed {
    val reducedMotion = isReducedMotion()
    if (reducedMotion) {
        return@composed this
    }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1.0f,
        animationSpec = tween(
            durationMillis = if (isPressed) MotionTokens.Fast else 140,
            easing = MotionTokens.EasingStandard
        ),
        label = "press_scale_anim"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
