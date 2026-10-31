package com.investkaro.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investkaro.formatting.IndianCurrencyFormatter
import com.investkaro.ui.theme.MotionTokens
import com.investkaro.ui.theme.Spacing
import com.investkaro.ui.theme.isReducedMotion
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * A compact, high-craft Money Summary Header placed at the top of Invest Karo and EMI Calculate tabs.
 * Dynamically scales font size to fit long amounts without clipping, and applies restrained transitions.
 * Supports smooth count-up animation for numeric BigDecimal values with 280ms debounce.
 */
@Composable
fun MoneySummaryHeader(
    label: String,
    amountText: String? = null,
    rawAmount: BigDecimal? = null,
    emptyPrompt: String? = null,
    secondaryChipText: String? = null,
    secondaryChipColor: Color? = null,
    footnote: String? = null,
    modifier: Modifier = Modifier
) {
    val reducedMotion = isReducedMotion()

    // Smooth counting state
    var displayedValue by remember { mutableStateOf(rawAmount) }
    val animProgress = remember { Animatable(1f) }

    LaunchedEffect(rawAmount) {
        if (rawAmount == null) {
            displayedValue = null
            animProgress.snapTo(1f)
            return@LaunchedEffect
        }

        val from = displayedValue ?: BigDecimal.ZERO

        if (reducedMotion || from.compareTo(rawAmount) == 0) {
            displayedValue = rawAmount
            animProgress.snapTo(1f)
        } else {
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = MotionTokens.SpringCounter
            ) {
                val progress = BigDecimal(value.toDouble())
                val delta = rawAmount.subtract(from)
                displayedValue = from.add(delta.multiply(progress))
            }
            displayedValue = rawAmount
        }
    }

    val finalFormattedText = rawAmount?.let { IndianCurrencyFormatter.formatRupees(it) } ?: amountText
    val currentFormattedText = when {
        rawAmount != null -> displayedValue?.let { IndianCurrencyFormatter.formatRupees(it) } ?: finalFormattedText ?: "—"
        amountText != null -> amountText
        else -> "—"
    }

    val isAmountAvailable = (rawAmount != null || amountText != null)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.cardCornerRadius))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(Spacing.cardCornerRadius)
            ),
        shape = RoundedCornerShape(Spacing.cardCornerRadius),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
        ) {
            // Label Row with optional secondary chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!secondaryChipText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                secondaryChipColor?.copy(alpha = 0.12f)
                                    ?: MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(horizontal = Spacing.xs, vertical = 2.dp)
                    ) {
                        Text(
                            text = secondaryChipText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = secondaryChipColor ?: MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Main Auto-Sizing Amount Display with Accessibility semantics
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        if (finalFormattedText != null) {
                            contentDescription = "$label: $finalFormattedText"
                        }
                    }
            ) {
                val widthDp = maxWidth

                // Auto-sizing typography based on display string length
                val targetFontSize: Float = when {
                    !isAmountAvailable -> 28f
                    widthDp < 300.dp && currentFormattedText.length > 12 -> 20f
                    widthDp < 340.dp && currentFormattedText.length > 10 -> 22f
                    currentFormattedText.length > 14 -> 22f
                    currentFormattedText.length > 11 -> 26f
                    currentFormattedText.length > 8 -> 30f
                    else -> 36f
                }

                val animatedFontSize by animateFloatAsState(
                    targetValue = targetFontSize,
                    animationSpec = tween(
                        durationMillis = if (reducedMotion) 0 else MotionTokens.Standard,
                        easing = MotionTokens.EasingStandard
                    ),
                    label = "money_header_font_size"
                )

                val fontSize: TextUnit = animatedFontSize.sp
                val lineHeight = (animatedFontSize * 1.15f).sp

                Text(
                    text = currentFormattedText,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    color = if (isAmountAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFeatureSettings = "tnum"
                    )
                )
            }

            if (!isAmountAvailable && !emptyPrompt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = emptyPrompt,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!footnote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = footnote,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
