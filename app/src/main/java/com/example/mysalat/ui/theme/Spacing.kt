package com.example.mysalat.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Strict spacing scale — layouts use only these tokens, never raw dp values.
 */
object Spacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
}

/**
 * Corner radii. Cards default to [lg] (24dp) for the premium, soft look.
 */
object Radius {
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val pill: Dp = 999.dp
}

/**
 * Elevation / border thicknesses used by the custom components.
 */
object Elevation {
    val none: Dp = 0.dp
    val card: Dp = 2.dp
    val raised: Dp = 8.dp
    val floating: Dp = 16.dp
    val hairline: Dp = 1.dp
}
