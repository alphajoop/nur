package com.example.mysalat.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.Spacing

/**
 * Section label with optional trailing action, used above every content block.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        action?.invoke()
    }
}

/**
 * Title plus supporting line, sharing consistent spacing and colours.
 */
@Composable
fun TitledBlock(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
    ) {
        Text(text = title, style = titleStyle, color = titleColor)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = subtitleColor
        )
    }
}

/**
 * Number that slides up when it increases and down when it decreases,
 * so streaks and countdowns never jump abruptly.
 */
@Composable
fun AnimatedCounter(
    value: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            val forward = (targetState.length > initialState.length) ||
                (targetState.length == initialState.length && targetState > initialState)
            val direction = if (forward) 1 else -1

            (slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) { height -> direction * height } + fadeIn(Motion.fadeFast()))
                .togetherWith(
                    slideOutVertically { height -> -direction * height } +
                        fadeOut(Motion.fadeFast())
                )
                .using(SizeTransform(clip = false))
        },
        label = "animatedCounter",
        modifier = modifier
    ) { current ->
        Text(text = current, style = style, color = color)
    }
}

/**
 * Renders a numeric string one character at a time so only the digits that
 * actually change animate. Keeps a ticking countdown calm instead of having the
 * whole line slide every second.
 */
@Composable
fun AnimatedDigits(
    value: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        value.forEachIndexed { index, char ->
            if (char.isDigit()) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        (slideInVertically(Motion.fadeFast()) { height -> height } +
                            fadeIn(Motion.fadeFast()))
                            .togetherWith(
                                slideOutVertically(Motion.fadeFast()) { height -> -height } +
                                    fadeOut(Motion.fadeFast())
                            )
                            .using(SizeTransform(clip = false))
                    },
                    label = "digit$index"
                ) { digit ->
                    Text(text = digit.toString(), style = style, color = color)
                }
            } else {
                Text(text = char.toString(), style = style, color = color)
            }
        }
    }
}
