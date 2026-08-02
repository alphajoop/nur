package com.example.mysalat.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Centralised motion tokens. Every animated state change in the app pulls its
 * spec from here so nothing ever snaps or feels mechanical.
 */
object Motion {

    /** Apple-like ease for fades and slides. */
    val standardEasing: Easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

    const val DurationFast = 180
    const val DurationMedium = 320
    const val DurationSlow = 520

    /** Smooth, no overshoot — colours, progress, layout size. */
    fun <T> springSoft(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    /** Slow and airy — hero entrances. */
    fun <T> springGentle(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Playful overshoot — check marks, streak pops, press feedback. */
    fun <T> springBouncy(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /** Quick and precise — selection indicators. */
    fun <T> springSnappy(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> fadeFast(): FiniteAnimationSpec<T> =
        tween(durationMillis = DurationFast, easing = standardEasing)

    fun <T> fadeMedium(): FiniteAnimationSpec<T> =
        tween(durationMillis = DurationMedium, easing = standardEasing)

    fun <T> fadeSlow(): FiniteAnimationSpec<T> =
        tween(durationMillis = DurationSlow, easing = standardEasing)
}
