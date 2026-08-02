package com.example.mysalat.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mysalat.ui.icons.AppIcon
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.theme.Radius

/**
 * Rounded, tinted container holding a single icon — the recurring visual motif
 * on prayer rows, quick actions and card headers.
 */
@Composable
fun IconBadge(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    tint: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 44.dp,
    iconSize: Dp = IconSize.lg,
    cornerRadius: Dp = Radius.md
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(
            icon = icon,
            contentDescription = contentDescription,
            size = iconSize,
            tint = tint
        )
    }
}

/**
 * Gradient variant for emphasis contexts such as the hero card.
 */
@Composable
fun GradientIconBadge(
    @DrawableRes icon: Int,
    brush: Brush,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 44.dp,
    iconSize: Dp = IconSize.lg,
    cornerRadius: Dp = Radius.md
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(
            icon = icon,
            contentDescription = contentDescription,
            size = iconSize,
            tint = tint
        )
    }
}
