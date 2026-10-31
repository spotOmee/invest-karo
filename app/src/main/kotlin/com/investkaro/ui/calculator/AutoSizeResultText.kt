package com.investkaro.ui.calculator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A measured, responsive text display for large currency values.
 * Automatically scales font size based on container width and string length
 * to prevent clipping, overflow, or awkward line wrapping.
 */
@Composable
fun AutoSizeResultText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = when (textAlign) {
            TextAlign.Center -> Alignment.Center
            TextAlign.End -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        val widthDp = maxWidth

        // Adapt font size dynamically based on text length and available width
        val fontSize: TextUnit = when {
            widthDp < 300.dp && text.length > 11 -> 24.sp
            widthDp < 340.dp && text.length > 10 -> 26.sp
            text.length > 14 -> 24.sp
            text.length > 12 -> 28.sp
            text.length > 9 -> 32.sp
            else -> 38.sp
        }

        val lineHeight = (fontSize.value * 1.15f).sp

        AnimatedContent(
            targetState = text,
            transitionSpec = {
                // Restrained 160ms subtle fade transition
                fadeIn(animationSpec = tween(durationMillis = 160)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 120))
            },
            label = "result_amount_anim"
        ) { targetText ->
            Text(
                text = targetText,
                color = color,
                fontSize = fontSize,
                lineHeight = lineHeight,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                textAlign = textAlign,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFeatureSettings = "tnum"
                )
            )
        }
    }
}
