package com.example.mysalat.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Brand tokens that Material 3's [androidx.compose.material3.ColorScheme] cannot express:
 * card treatments, gradients, glass surfaces and the gold accent.
 */
data class MySalatColors(
    val cardSurface: Color,
    val cardBorder: Color,
    val glassSurface: Color,
    val shadow: Color,
    val gold: Color,
    val goldWash: Color,
    val accent: Color,
    val greenWash: Color,
    val prayerIdleSurface: Color,
    val prayerDoneSurface: Color,
    val heroGradient: List<Color>,
    val accentGradient: List<Color>,
    val ringTrack: Color,
    val isDark: Boolean
) {
    /** Left-to-right hero gradient. */
    val heroBrush: Brush get() = Brush.linearGradient(heroGradient)

    /** Gradient used by primary buttons and the progress ring sweep. */
    val accentBrush: Brush get() = Brush.linearGradient(accentGradient)
}

private val LightMySalatColors = MySalatColors(
    cardSurface = LightCard,
    cardBorder = LightCardBorder,
    glassSurface = LightGlass,
    shadow = LightShadow,
    gold = BrandGold,
    goldWash = LightGoldWash,
    accent = BrandAccent,
    greenWash = LightGreenWash,
    prayerIdleSurface = LightCard,
    prayerDoneSurface = LightGreenWash,
    heroGradient = listOf(BrandGreenDeep, BrandGreen, Color(0xFF14958A)),
    accentGradient = listOf(BrandGreen, BrandAccent),
    ringTrack = LightSurfaceMuted,
    isDark = false
)

private val DarkMySalatColors = MySalatColors(
    cardSurface = DarkCard,
    cardBorder = DarkCardBorder,
    glassSurface = DarkGlass,
    shadow = DarkShadow,
    gold = BrandGoldOnDark,
    goldWash = DarkGoldWash,
    accent = BrandAccentOnDark,
    greenWash = DarkGreenWash,
    prayerIdleSurface = DarkCard,
    prayerDoneSurface = DarkGreenWash,
    heroGradient = listOf(Color(0xFF0A3F3A), Color(0xFF0F5F58), Color(0xFF14807A)),
    accentGradient = listOf(BrandGreenOnDark, BrandAccentOnDark),
    ringTrack = DarkSurfaceMuted,
    isDark = true
)

private val LocalMySalatColors = staticCompositionLocalOf { LightMySalatColors }

/** Access brand tokens: `MaterialTheme.brand.cardSurface`. */
val MaterialTheme.brand: MySalatColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMySalatColors.current

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    primaryContainer = LightGreenWash,
    onPrimaryContainer = BrandGreenDeep,
    secondary = BrandAccent,
    onSecondary = Color(0xFF04322D),
    secondaryContainer = LightGreenWash,
    onSecondaryContainer = BrandGreenDeep,
    tertiary = BrandGold,
    onTertiary = Color.White,
    tertiaryContainer = LightGoldWash,
    onTertiaryContainer = Color(0xFF5A4A14),
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightCard,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceMuted,
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline,
    outlineVariant = LightCardBorder,
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreenOnDark,
    onPrimary = Color(0xFF04302B),
    primaryContainer = DarkGreenWash,
    onPrimaryContainer = BrandAccentOnDark,
    secondary = BrandAccentOnDark,
    onSecondary = Color(0xFF04302B),
    secondaryContainer = DarkGreenWash,
    onSecondaryContainer = BrandAccentOnDark,
    tertiary = BrandGoldOnDark,
    onTertiary = Color(0xFF2A2210),
    tertiaryContainer = DarkGoldWash,
    onTertiaryContainer = BrandGoldOnDark,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceMuted,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    outlineVariant = DarkCardBorder,
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun MySalatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val brandColors = if (darkTheme) DarkMySalatColors else LightMySalatColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        val context = LocalContext.current
        SideEffect {
            (context as? Activity)?.window?.let { window ->
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalMySalatColors provides brandColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = MySalatShapes,
            content = content
        )
    }
}
