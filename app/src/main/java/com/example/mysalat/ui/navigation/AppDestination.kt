package com.example.mysalat.ui.navigation

import androidx.annotation.DrawableRes
import com.example.mysalat.ui.icons.AppIcons

/**
 * The five top-level destinations. Deliberately a plain enum rather than
 * `navigation-compose`: the app has no nested graphs or arguments to pass.
 */
enum class AppDestination(
    val label: String,
    @param:DrawableRes val icon: Int
) {
    Home("Accueil", AppIcons.Home),
    Quran("Coran", AppIcons.Quran),
    Qibla("Qibla", AppIcons.Qibla),
    History("Historique", AppIcons.History),
    Profile("Profil", AppIcons.Profile);

    companion object {
        val tabs: List<AppDestination> = entries
    }
}
