package com.example.mysalat.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

/**
 * Global shape set so any residual Material component inherits the soft,
 * premium corner language instead of the default 4dp/8dp radii.
 */
val MySalatShapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.sm),
    small = RoundedCornerShape(Radius.md),
    medium = RoundedCornerShape(Radius.lg),
    large = RoundedCornerShape(Radius.lg),
    extraLarge = RoundedCornerShape(Radius.xl)
)

val CardShape = RoundedCornerShape(Radius.lg)
val HeroShape = RoundedCornerShape(Radius.xl)
val PillShape = RoundedCornerShape(Radius.pill)
