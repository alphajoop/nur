package com.example.mysalat.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.mysalat.PrayerRowState
import com.example.mysalat.data.Prayer
import com.example.mysalat.ui.components.IconBadge
import com.example.mysalat.ui.components.ModernCheckbox
import com.example.mysalat.ui.icons.AppIcons
import com.example.mysalat.ui.theme.Elevation
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.Radius
import com.example.mysalat.ui.theme.Spacing
import com.example.mysalat.ui.theme.brand

/** Maps each prayer to the icon matching its place in the day's arc. */
private fun iconFor(prayer: Prayer): Int = when (prayer) {
    Prayer.FAJR -> AppIcons.Fajr
    Prayer.DHUHR -> AppIcons.Dhuhr
    Prayer.ASR -> AppIcons.Asr
    Prayer.MAGHRIB -> AppIcons.Maghrib
    Prayer.ISHA -> AppIcons.Isha
}

/**
 * One tappable prayer row. The whole card is the hit target; the checkbox is
 * decorative. Completing a prayer washes the surface green, pops the card and
 * fires a confirm haptic.
 */
@Composable
fun PrayerCard(
    row: PrayerRowState,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val brand = MaterialTheme.brand
    val colorScheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val surface by animateColorAsState(
        targetValue = if (row.completed) brand.prayerDoneSurface else brand.prayerIdleSurface,
        animationSpec = Motion.springSoft(),
        label = "prayerSurface"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            row.completed -> colorScheme.primary.copy(alpha = 0.35f)
            row.isNext -> colorScheme.primary.copy(alpha = 0.5f)
            else -> brand.cardBorder
        },
        animationSpec = Motion.springSoft(),
        label = "prayerBorder"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = Motion.springBouncy(),
        label = "prayerScale"
    )

    val shape = RoundedCornerShape(Radius.lg)
    val stateLabel = if (row.completed) "Accomplie" else "Non accomplie"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(shape)
            .background(surface)
            .border(Elevation.hairline, borderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                role = Role.Checkbox,
                onClick = {
                    haptic.performHapticFeedback(
                        if (!row.completed) {
                            HapticFeedbackType.Confirm
                        } else {
                            HapticFeedbackType.ContextClick
                        }
                    )
                    onToggle(!row.completed)
                }
            )
            .animateContentSize(Motion.springSoft())
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            .semantics {
                contentDescription = "${row.prayer.displayName} à ${row.time}"
                stateDescription = stateLabel
            },
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBadge(
            icon = iconFor(row.prayer),
            containerColor = if (row.completed) {
                colorScheme.primary.copy(alpha = 0.14f)
            } else {
                colorScheme.surfaceVariant
            },
            tint = if (row.completed) colorScheme.primary else colorScheme.onSurfaceVariant,
            size = 44.dp
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = row.prayer.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Times already behind us recede slightly rather than being flagged.
                val timeAlpha = if (row.hasPassed && !row.completed) 0.6f else 1f
                Text(
                    text = row.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = timeAlpha)
                )
                AnimatedVisibility(
                    visible = row.isNext && !row.completed,
                    enter = fadeIn(Motion.fadeFast()) + expandHorizontally(Motion.springSoft()),
                    exit = fadeOut(Motion.fadeFast()) + shrinkHorizontally(Motion.springSoft())
                ) {
                    NextPill()
                }
            }
        }

        ModernCheckbox(checked = row.completed)
    }
}

@Composable
private fun NextPill(modifier: Modifier = Modifier) {
    Text(
        text = "à venir",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.pill))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = Spacing.xs, vertical = 2.dp)
    )
}
