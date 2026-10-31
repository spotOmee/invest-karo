package com.investkaro.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investkaro.ui.theme.MotionTokens
import com.investkaro.ui.theme.Spacing

/**
 * Destinations required by product specification:
 * 1. Invest Karo
 * 2. EMI Calculate
 */
enum class AppDestination(
    val title: String,
    val icon: ImageVector
) {
    INVEST_KARO("Invest Karo", Icons.AutoMirrored.Filled.TrendingUp),
    EMI_CALCULATE("EMI Calculate", Icons.Default.Calculate)
}

@Composable
fun InvestKaroBottomBar(
    currentDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppDestination.entries.forEach { destination ->
                    val isSelected = destination == currentDestination

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = MotionTokens.Standard, easing = MotionTokens.EasingStandard),
                        label = "nav_icon_color"
                    )

                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = MotionTokens.Standard, easing = MotionTokens.EasingStandard),
                        label = "nav_text_color"
                    )

                    val pillBg by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent,
                        animationSpec = tween(durationMillis = MotionTokens.Standard, easing = MotionTokens.EasingStandard),
                        label = "nav_pill_bg"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.0f else 0.96f,
                        animationSpec = tween(durationMillis = MotionTokens.Standard, easing = MotionTokens.EasingStandard),
                        label = "nav_scale"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(pillBg)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onDestinationSelected(destination)
                            }
                            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                            .semantics {
                                role = Role.Tab
                                contentDescription = destination.title
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = destination.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}
