package com.example.mysalat.ui.icons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.R as LucideR

/**
 * Single mapping point between the app's semantic icon names and the Lucide set.
 * Screens never reference Lucide drawables directly, so swapping the icon pack
 * only touches this file.
 */
object AppIcons {

    // Prayers — the arc of the day, dawn through night
    @DrawableRes val Fajr = LucideR.drawable.lucide_ic_sunrise
    @DrawableRes val Dhuhr = LucideR.drawable.lucide_ic_sun
    @DrawableRes val Asr = LucideR.drawable.lucide_ic_cloud_sun
    @DrawableRes val Maghrib = LucideR.drawable.lucide_ic_sunset
    @DrawableRes val Isha = LucideR.drawable.lucide_ic_moon

    // Identity and status
    @DrawableRes val Moon = LucideR.drawable.lucide_ic_moon_star
    @DrawableRes val Flame = LucideR.drawable.lucide_ic_flame
    @DrawableRes val Sparkles = LucideR.drawable.lucide_ic_sparkles
    @DrawableRes val Star = LucideR.drawable.lucide_ic_star
    @DrawableRes val Check = LucideR.drawable.lucide_ic_check
    @DrawableRes val CheckAll = LucideR.drawable.lucide_ic_check_check
    @DrawableRes val Clock = LucideR.drawable.lucide_ic_clock_fading

    // Navigation
    @DrawableRes val Home = LucideR.drawable.lucide_ic_house
    @DrawableRes val Quran = LucideR.drawable.lucide_ic_book_open
    @DrawableRes val Qibla = LucideR.drawable.lucide_ic_compass
    @DrawableRes val History = LucideR.drawable.lucide_ic_chart_column
    @DrawableRes val Profile = LucideR.drawable.lucide_ic_user_round

    // Quick actions
    @DrawableRes val Tasbih = LucideR.drawable.lucide_ic_repeat
    @DrawableRes val Hijri = LucideR.drawable.lucide_ic_calendar_days
    @DrawableRes val Dua = LucideR.drawable.lucide_ic_heart_handshake
    @DrawableRes val Mosque = LucideR.drawable.lucide_ic_landmark

    // Actions and affordances
    @DrawableRes val Share = LucideR.drawable.lucide_ic_share_2
    @DrawableRes val Quote = LucideR.drawable.lucide_ic_quote
    @DrawableRes val Back = LucideR.drawable.lucide_ic_arrow_left
    @DrawableRes val ChevronRight = LucideR.drawable.lucide_ic_chevron_right
    @DrawableRes val Settings = LucideR.drawable.lucide_ic_settings
    @DrawableRes val Bell = LucideR.drawable.lucide_ic_bell
}

object IconSize {
    val sm: Dp = 16.dp
    val md: Dp = 20.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
}

/**
 * Draws a [AppIcons] entry with a tint, keeping icon sizing consistent app-wide.
 */
@Composable
fun AppIcon(
    @DrawableRes icon: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = IconSize.lg,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size)
    )
}
