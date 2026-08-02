package com.example.mysalat.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.example.mysalat.ui.theme.Elevation
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.Radius
import com.example.mysalat.ui.theme.brand
import androidx.compose.foundation.clickable as foundationClickable

/**
 * The base card of the whole app: soft shadow, hairline border, 24dp corners.
 * Optionally interactive, in which case it scales down slightly while pressed.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Radius.lg,
    elevation: Dp = Elevation.raised,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val brand = MaterialTheme.brand
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && onClick != null) 0.97f else 1f,
        animationSpec = Motion.springBouncy(),
        label = "glassCardScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = brand.shadow,
                spotColor = brand.shadow
            )
            .clip(shape)
            .background(brand.cardSurface)
            .border(Elevation.hairline, brand.cardBorder, shape)
            .then(
                if (onClick != null) {
                    Modifier.foundationClickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        content = content
    )
}

/**
 * Gradient panel used by the hero card and other emphasis surfaces.
 */
@Composable
fun GradientSurface(
    modifier: Modifier = Modifier,
    brush: Brush = MaterialTheme.brand.heroBrush,
    cornerRadius: Dp = Radius.xl,
    elevation: Dp = Elevation.floating,
    content: @Composable BoxScope.() -> Unit
) {
    val brand = MaterialTheme.brand
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = brand.shadow,
                spotColor = brand.shadow
            )
            .clip(shape)
            .background(brush),
        content = content
    )
}

/**
 * Translucent "glass" panel: used behind the floating bottom bar so content
 * scrolling underneath stays subtly visible.
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Radius.pill,
    elevation: Dp = Elevation.floating,
    content: @Composable BoxScope.() -> Unit
) {
    val brand = MaterialTheme.brand
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = brand.shadow,
                spotColor = brand.shadow
            )
            .clip(shape)
            .background(brand.glassSurface)
            .border(Elevation.hairline, brand.cardBorder, shape),
        content = content
    )
}
