package com.example.mysalat.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mysalat.ui.components.GlassCard
import com.example.mysalat.ui.components.IconBadge
import com.example.mysalat.ui.icons.AppIcons
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.theme.Spacing

/** A single shortcut tile definition. */
data class QuickAction(
    val label: String,
    @param:DrawableRes val icon: Int
)

private val actions = listOf(
    QuickAction("Qibla", AppIcons.Qibla),
    QuickAction("Tasbih", AppIcons.Tasbih),
    QuickAction("Coran", AppIcons.Quran),
    QuickAction("Hijri", AppIcons.Hijri),
    QuickAction("Invocations", AppIcons.Dua),
    QuickAction("Mosquées", AppIcons.Mosque)
)

/**
 * Three-column shortcut grid. Built from plain rows rather than a lazy grid so
 * it nests inside the home screen's single scrolling column.
 */
@Composable
fun QuickActionsGrid(
    onActionClick: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        actions.chunked(3).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                rowActions.forEach { action ->
                    QuickActionTile(
                        action = action,
                        onClick = { onActionClick(action) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Keeps tile widths consistent when the last row is partial.
                repeat(3 - rowActions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    GlassCard(
        modifier = modifier,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md, horizontal = Spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            IconBadge(
                icon = action.icon,
                size = 40.dp,
                iconSize = IconSize.md
            )
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
