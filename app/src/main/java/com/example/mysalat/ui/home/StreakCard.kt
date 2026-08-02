package com.example.mysalat.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mysalat.ui.components.AnimatedCounter
import com.example.mysalat.ui.components.GlassCard
import com.example.mysalat.ui.components.IconBadge
import com.example.mysalat.ui.icons.AppIcons
import com.example.mysalat.ui.theme.Elevation
import com.example.mysalat.ui.theme.Radius
import com.example.mysalat.ui.theme.Spacing
import com.example.mysalat.ui.theme.brand

/**
 * Streak counter with a flame badge and a gold hairline that appears only once
 * a streak is running, so the reward feels earned.
 */
@Composable
fun StreakCard(
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    val brand = MaterialTheme.brand
    val active = streakCount > 0
    val label = if (streakCount == 1) "jour d'affilée" else "jours d'affilée"

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Série de $streakCount $label"
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (active) {
                        Modifier.border(
                            width = Elevation.hairline,
                            color = brand.gold.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(Radius.lg)
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(
                icon = AppIcons.Flame,
                containerColor = if (active) brand.goldWash else MaterialTheme.colorScheme.surfaceVariant,
                tint = if (active) brand.gold else MaterialTheme.colorScheme.onSurfaceVariant,
                size = 48.dp
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                    verticalAlignment = Alignment.Bottom
                ) {
                    AnimatedCounter(
                        value = streakCount.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = if (active) "Continuez votre série" else "Complétez vos 5 prières pour démarrer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
