package com.example.mysalat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.brand

/**
 * Pill switch with a spring-driven thumb. Fires toggle haptics on change.
 */
@Composable
fun ModernSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val brand = MaterialTheme.brand
    val colorScheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current

    val trackWidth = 52.dp
    val trackHeight = 32.dp
    val thumbSize = 26.dp
    val travel = trackWidth - thumbSize - 6.dp

    val trackColor by animateColorAsState(
        targetValue = if (checked) colorScheme.primary else colorScheme.surfaceVariant,
        animationSpec = Motion.springSoft(),
        label = "switchTrack"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) travel else 0.dp,
        animationSpec = Motion.springBouncy(),
        label = "switchThumb"
    )

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch,
                onClick = {
                    haptic.performHapticFeedback(
                        if (!checked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                    )
                    onCheckedChange(!checked)
                }
            )
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(4.dp, CircleShape, ambientColor = brand.shadow, spotColor = brand.shadow)
                .clip(CircleShape)
                .background(colorScheme.surface)
        )
    }
}
