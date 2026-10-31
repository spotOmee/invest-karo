package com.investkaro.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EEDF),
    onPrimaryContainer = Color(0xFF002111),
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightSurface,
    onSurface = LightPrimaryText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorder,
    outlineVariant = Color(0xFFD0CEC6),
    error = LightError,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = Color(0xFF2B1700),
    primaryContainer = Color(0xFF6B3B00),
    onPrimaryContainer = Color(0xFFFFDCC1),
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkSecondaryText,
    surfaceContainer = DarkSurfaceElevated,
    outline = DarkBorder,
    outlineVariant = Color(0xFF43453D),
    error = DarkError,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun InvestKaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val targetColors = if (darkTheme) DarkColorScheme else LightColorScheme
    val duration = if (isReducedMotion()) 0 else MotionTokens.Standard

    val animatedBackground = animateColorAsState(
        targetValue = targetColors.background,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "bg_color"
    ).value

    val animatedSurface = animateColorAsState(
        targetValue = targetColors.surface,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "surface_color"
    ).value

    val animatedSurfaceVariant = animateColorAsState(
        targetValue = targetColors.surfaceVariant,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "surface_variant_color"
    ).value

    val animatedPrimary = animateColorAsState(
        targetValue = targetColors.primary,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "primary_color"
    ).value

    val animatedOnPrimary = animateColorAsState(
        targetValue = targetColors.onPrimary,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "on_primary_color"
    ).value

    val animatedOnBackground = animateColorAsState(
        targetValue = targetColors.onBackground,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "on_bg_color"
    ).value

    val animatedOnSurface = animateColorAsState(
        targetValue = targetColors.onSurface,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "on_surface_color"
    ).value

    val animatedOnSurfaceVariant = animateColorAsState(
        targetValue = targetColors.onSurfaceVariant,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "on_surface_variant_color"
    ).value

    val animatedOutline = animateColorAsState(
        targetValue = targetColors.outline,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "outline_color"
    ).value

    val animatedOutlineVariant = animateColorAsState(
        targetValue = targetColors.outlineVariant,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "outline_variant_color"
    ).value

    val animatedError = animateColorAsState(
        targetValue = targetColors.error,
        animationSpec = tween(durationMillis = duration, easing = MotionTokens.EasingStandard),
        label = "error_color"
    ).value

    val animatedColorScheme = targetColors.copy(
        background = animatedBackground,
        surface = animatedSurface,
        surfaceVariant = animatedSurfaceVariant,
        primary = animatedPrimary,
        onPrimary = animatedOnPrimary,
        onBackground = animatedOnBackground,
        onSurface = animatedOnSurface,
        onSurfaceVariant = animatedOnSurfaceVariant,
        outline = animatedOutline,
        outlineVariant = animatedOutlineVariant,
        error = animatedError
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = Typography,
        content = content
    )
}