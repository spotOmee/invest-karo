package com.investkaro.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.investkaro.ui.theme.MotionTokens
import com.investkaro.ui.theme.Spacing
import com.investkaro.ui.theme.isReducedMotion

/**
 * High-craft segmented pill control that physically slides an animated layout pill indicator
 * between options using spring physics instead of instant snapping.
 */
@Composable
fun <T> SmoothSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 42.dp,
    contentPadding: Dp = 4.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    pillColor: Color = MaterialTheme.colorScheme.primary,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shape: Shape = RoundedCornerShape(Spacing.inputCornerRadius),
    pillShape: Shape = RoundedCornerShape(Spacing.inputCornerRadius - 2.dp),
    itemLabel: (T) -> String
) {
    val reducedMotion = isReducedMotion()
    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)
    val numItems = items.size.coerceAtLeast(1)

    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = if (reducedMotion) tween(0) else MotionTokens.SpringPill,
        label = "segmented_pill_offset"
    )

    Surface(
        modifier = modifier
            .clip(shape)
            .border(1.dp, borderColor, shape),
        shape = shape,
        color = containerColor
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(height)
                .padding(contentPadding)
        ) {
            val totalWidth = maxWidth
            val itemWidth = totalWidth / numItems

            // Sliding background pill indicator
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .fillMaxHeight()
                    .offset(x = itemWidth * animatedIndex)
                    .clip(pillShape)
                    .background(pillColor)
            )

            // Foreground labels row
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) selectedTextColor else unselectedTextColor,
                        animationSpec = tween(MotionTokens.Fast),
                        label = "segmented_text_color"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(pillShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onItemSelected(item) }
                            .semantics { role = Role.Tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = itemLabel(item),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
