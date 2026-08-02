package com.example.mysalat.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.mysalat.ui.icons.AppIcon
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.theme.Elevation
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.PillShape
import com.example.mysalat.ui.theme.Radius
import com.example.mysalat.ui.theme.brand

enum class ButtonVariant {
    /** Filled gradient — the single strongest action on a screen. */
    Primary,

    /** Outlined, transparent background. */
    Secondary,

    /** Text-only, lowest emphasis. */
    Ghost
}

/**
 * The app's only button. Scales on press, uses a tinted ripple and never shows
 * a default Material filled surface.
 */
@Composable
fun ModernButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    @DrawableRes icon: Int? = null,
    enabled: Boolean = true
) {
    val brand = MaterialTheme.brand
    val colorScheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = Motion.springBouncy(),
        label = "buttonScale"
    )

    val contentColor = when (variant) {
        ButtonVariant.Primary -> colorScheme.onPrimary
        ButtonVariant.Secondary, ButtonVariant.Ghost -> colorScheme.primary
    }
    val alpha = if (enabled) 1f else 0.4f

    val base = Modifier
        .scale(scale)
        .defaultMinSize(minHeight = 52.dp)

    val styled = when (variant) {
        ButtonVariant.Primary -> base
            .shadow(
                elevation = Elevation.raised,
                shape = PillShape,
                ambientColor = brand.shadow,
                spotColor = brand.shadow
            )
            .clip(PillShape)
            .background(brand.accentBrush)

        ButtonVariant.Secondary -> base
            .clip(PillShape)
            .border(Elevation.hairline, colorScheme.primary.copy(alpha = 0.45f), PillShape)

        ButtonVariant.Ghost -> base
            .clip(RoundedCornerShape(Radius.md))
            .background(Color.Transparent)
    }

    Row(
        modifier = modifier
            .then(styled)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            AppIcon(
                icon = icon,
                contentDescription = null,
                size = IconSize.md,
                tint = contentColor.copy(alpha = alpha)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor.copy(alpha = alpha)
        )
    }
}
