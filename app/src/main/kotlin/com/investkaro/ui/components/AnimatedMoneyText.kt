package com.investkaro.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.investkaro.formatting.IndianCurrencyFormatter
import com.investkaro.ui.theme.MotionTokens
import com.investkaro.ui.theme.isReducedMotion
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Animated numeric counter displaying currency formatted according to the Indian numbering system.
 * Interpolates smoothly between values using spring physics (duration: 350-500ms, stiffness: 220, damping: ~28)
 * and tabular figures (tnum) to eliminate layout jitter and flickering.
 */
@Composable
fun AnimatedMoneyText(
    amount: BigDecimal?,
    modifier: Modifier = Modifier,
    emptyText: String = "—",
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = FontWeight.SemiBold,
    color: Color? = null,
    positiveColor: Color = MaterialTheme.colorScheme.primary,
    negativeColor: Color = MaterialTheme.colorScheme.error,
    isDynamicColorBySign: Boolean = false,
    includeSymbol: Boolean = true,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val reducedMotion = isReducedMotion()

    var displayedValue by remember { mutableStateOf(amount) }
    val animProgress = remember { Animatable(1f) }

    LaunchedEffect(amount) {
        if (amount == null) {
            displayedValue = null
            animProgress.snapTo(1f)
            return@LaunchedEffect
        }

        val from = displayedValue ?: BigDecimal.ZERO

        if (reducedMotion || from.compareTo(amount) == 0) {
            displayedValue = amount
            animProgress.snapTo(1f)
        } else {
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = MotionTokens.SpringCounter
            ) {
                val progress = BigDecimal(value.toDouble())
                val delta = amount.subtract(from)
                displayedValue = from.add(delta.multiply(progress))
            }
            displayedValue = amount
        }
    }

    val currentAmount = displayedValue ?: amount
    val formattedText = when {
        currentAmount != null -> IndianCurrencyFormatter.formatRupees(
            currentAmount.setScale(0, RoundingMode.HALF_UP),
            includeSymbol = includeSymbol
        )
        else -> emptyText
    }

    val targetColor = when {
        isDynamicColorBySign -> {
            val isNegative = (currentAmount ?: BigDecimal.ZERO) < BigDecimal.ZERO
            if (isNegative) negativeColor else positiveColor
        }
        color != null -> color
        else -> MaterialTheme.colorScheme.onSurface
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = MotionTokens.Fast),
        label = "money_text_color"
    )

    Text(
        text = formattedText,
        style = style.copy(
            fontFeatureSettings = "tnum"
        ),
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        color = animatedColor,
        modifier = modifier
    )
}
