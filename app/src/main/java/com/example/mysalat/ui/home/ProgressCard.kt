package com.example.mysalat.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mysalat.data.Prayer
import com.example.mysalat.ui.components.GlassCard
import com.example.mysalat.ui.components.ProgressRing
import com.example.mysalat.ui.theme.Spacing

/**
 * Animated ring showing today's completion, paired with a short encouragement
 * that changes with progress.
 */
@Composable
fun ProgressCard(
    completedCount: Int,
    progress: Float,
    encouragement: String,
    modifier: Modifier = Modifier
) {
    val percent = (progress * 100).toInt()

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription =
                    "Progression du jour : $completedCount sur ${Prayer.COUNT} prières"
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressRing(
                progress = progress,
                diameter = 104.dp,
                strokeWidth = 10.dp
            ) {
                Text(
                    text = "$completedCount/${Prayer.COUNT}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$percent %",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = "Aujourd'hui",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
