package com.example.mysalat

import com.example.mysalat.data.CityCatalog
import com.example.mysalat.data.NextPrayerInfo
import com.example.mysalat.data.Prayer
import com.example.mysalat.data.PrayerSchedule
import com.example.mysalat.data.PrayerStorage
import com.example.mysalat.data.PrayerTimesCalculator
import com.example.mysalat.data.Verse
import com.example.mysalat.data.VerseLibrary
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/** Time-of-day bucket driving the greeting copy and hero mood. */
enum class DayPart(val greeting: String) {
    Morning("Bonjour"),
    Afternoon("Bon après-midi"),
    Evening("Bonsoir"),
    Night("Bonne nuit");

    companion object {
        fun of(time: LocalTime): DayPart = when (time.hour) {
            in 5..11 -> Morning
            in 12..17 -> Afternoon
            in 18..21 -> Evening
            else -> Night
        }
    }
}

/**
 * Row model for a single prayer on the home screen.
 */
data class PrayerRowState(
    val prayer: Prayer,
    val time: String,
    val completed: Boolean,
    val isNext: Boolean,
    val hasPassed: Boolean
)

/**
 * Everything the home screen renders, recomputed as the clock ticks and as
 * completions / city change.
 */
data class HomeUiState(
    val userName: String = PrayerStorage.DEFAULT_USER_NAME,
    val cityName: String = CityCatalog.default.displayName,
    val dayPart: DayPart = DayPart.Morning,
    val formattedDate: String = "",
    val next: NextPrayerInfo = defaultNext(),
    val rows: List<PrayerRowState> = emptyList(),
    val completedCount: Int = 0,
    val streakCount: Int = 0,
    val verse: Verse = VerseLibrary.first
) {
    val progress: Float get() = completedCount / Prayer.COUNT.toFloat()
    val allCompleted: Boolean get() = completedCount == Prayer.COUNT
    val remaining: Duration get() = next.remaining

    /** Progress-aware encouragement shown under the ring. */
    val encouragement: String
        get() = when (completedCount) {
            0 -> "Commencez votre journée avec force"
            1 -> "Bon départ, continuez ainsi"
            2 -> "Vous êtes sur la bonne voie"
            3 -> "Plus que deux prières"
            4 -> "Une dernière prière, courage"
            else -> "Journée complète, qu'Allah accepte"
        }

    companion object {
        private fun defaultNext(): NextPrayerInfo {
            val today = LocalDate.now()
            val city = CityCatalog.default
            val times = PrayerTimesCalculator.timesFor(city, today)
            return PrayerSchedule.nextPrayer(LocalTime.of(0, 0), times)
        }
    }
}

/** French date such as "samedi 1 août 2026". */
fun LocalDate.toFrenchLongDate(): String {
    val days = listOf(
        "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"
    )
    val months = listOf(
        "janvier", "février", "mars", "avril", "mai", "juin",
        "juillet", "août", "septembre", "octobre", "novembre", "décembre"
    )
    return "${days[dayOfWeek.value - 1]} $dayOfMonth ${months[monthValue - 1]} $year"
}

/** Compact French date such as "sam. 1 août". */
fun LocalDate.toFrenchShortDate(): String {
    val days = listOf("lun.", "mar.", "mer.", "jeu.", "ven.", "sam.", "dim.")
    val months = listOf(
        "janv.", "févr.", "mars", "avr.", "mai", "juin",
        "juil.", "août", "sept.", "oct.", "nov.", "déc."
    )
    return "${days[dayOfWeek.value - 1]} $dayOfMonth ${months[monthValue - 1]}"
}
