package com.example.mysalat.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mysalat.ui.components.GlassPanel
import com.example.mysalat.ui.icons.AppIcon
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.Spacing

/**
 * Detached, pill-shaped tab bar sitting above the bottom edge. The selected tab
 * grows a tinted pill behind its icon and reveals its label; unselected tabs
 * stay icon-only so the bar remains compact.
 */
@Composable
fun FloatingBottomBar(
    current: AppDestination,
    onSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassPanel(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xs, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestination.tabs.forEach { destination ->
                BottomBarItem(
                    destination = destination,
                    selected = destination == current,
                    onClick = { onSelect(destination) }
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    destination: AppDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current

    val tint by animateColorAsState(
        targetValue = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant,
        animationSpec = Motion.springSoft(),
        label = "tabTint"
    )
    val indicator by animateColorAsState(
        targetValue = if (selected) colorScheme.primaryContainer else colorScheme.surface.copy(alpha = 0f),
        animationSpec = Motion.springSoft(),
        label = "tabIndicator"
    )
    val indicatorSize by animateDpAsState(
        targetValue = if (selected) 40.dp else 34.dp,
        animationSpec = Motion.springBouncy(),
        label = "tabIndicatorSize"
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = Motion.fadeFast(),
        label = "tabLabelAlpha"
    )

    Column(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = {
                    if (!selected) {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                    onClick()
                }
            )
            .padding(horizontal = Spacing.xs, vertical = Spacing.xxs)
            .semantics {
                this.selected = selected
                contentDescription = destination.label
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .clip(CircleShape)
                .background(indicator),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                icon = destination.icon,
                contentDescription = null,
                size = IconSize.md,
                tint = tint
            )
        }

        // Reserved height keeps the bar from resizing as selection moves.
        Box(
            modifier = Modifier.height(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = destination.label,
                style = MaterialTheme.typography.labelSmall,
                color = tint,
                modifier = Modifier.alpha(labelAlpha)
            )
        }
    }
}
