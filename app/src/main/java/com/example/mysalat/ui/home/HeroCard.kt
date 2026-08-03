package com.example.mysalat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mysalat.HomeUiState
import com.example.mysalat.data.toCountdownString
import com.example.mysalat.ui.components.AnimatedDigits
import com.example.mysalat.ui.components.GradientSurface
import com.example.mysalat.ui.icons.AppIcon
import com.example.mysalat.ui.icons.AppIcons
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.theme.CountdownDigitStyle
import com.example.mysalat.ui.theme.Radius
import com.example.mysalat.ui.theme.Spacing

/**
 * The screen's focal point: gradient panel announcing the next prayer with a
 * live countdown. Two soft translucent circles sit behind the content to give
 * the gradient depth without a real blur.
 */
@Composable
fun HeroCard(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    val onHero = Color.White

    GradientSurface(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = Radius.xl
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = 220.dp, y = (-70).dp)
                .clip(CircleShape)
                .background(onHero.copy(alpha = 0.10f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-40).dp, y = 110.dp)
                .clip(CircleShape)
                .background(onHero.copy(alpha = 0.07f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        icon = AppIcons.Moon,
                        contentDescription = null,
                        size = IconSize.md,
                        tint = onHero.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Prochaine prière",
                        style = MaterialTheme.typography.labelMedium,
                        color = onHero.copy(alpha = 0.85f)
                    )
                }
                if (state.next.isTomorrow) {
                    Text(
                        text = "demain · ${state.cityName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = onHero.copy(alpha = 0.75f)
                    )
                } else {
                    Text(
                        text = state.cityName,
                        style = MaterialTheme.typography.labelMedium,
                        color = onHero.copy(alpha = 0.75f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    Text(
                        text = state.next.prayer.displayName,
                        style = MaterialTheme.typography.displaySmall,
                        color = onHero
                    )
                    Text(
                        text = "à ${String.format("%02d:%02d", state.next.time.hour, state.next.time.minute)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = onHero.copy(alpha = 0.8f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
                ) {
                    AnimatedDigits(
                        value = state.remaining.toCountdownString(),
                        style = CountdownDigitStyle,
                        color = onHero
                    )
                    Text(
                        text = "restantes",
                        style = MaterialTheme.typography.labelSmall,
                        color = onHero.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
