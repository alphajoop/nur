package com.example.mysalat.data

import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Next prayer relative to a given moment, with the time left before it starts.
 */
data class NextPrayerInfo(
    val prayer: Prayer,
    val time: LocalTime,
    val remaining: Duration,
    /** True when the next occurrence is tomorrow's Fajr (i.e. after Isha). */
    val isTomorrow: Boolean
)

/**
 * Helpers over a day's prayer timetable (produced by [PrayerTimesCalculator]).
 */
object PrayerSchedule {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun formattedTime(time: LocalTime): String = formatter.format(time)

    fun formattedTime(prayer: Prayer, times: Map<Prayer, LocalTime>): String =
        formattedTime(times.getValue(prayer))

    /**
     * Returns the upcoming prayer for [now]. After Isha, wraps to [tomorrowFajr].
     */
    fun nextPrayer(
        now: LocalTime,
        times: Map<Prayer, LocalTime>,
        tomorrowFajr: LocalTime = times.getValue(Prayer.FAJR)
    ): NextPrayerInfo {
        val ordered = Prayer.entries.map { it to times.getValue(it) }
        val upcoming = ordered.firstOrNull { (_, time) -> time.isAfter(now) }

        return if (upcoming != null) {
            NextPrayerInfo(
                prayer = upcoming.first,
                time = upcoming.second,
                remaining = Duration.between(now, upcoming.second),
                isTomorrow = false
            )
        } else {
            val untilMidnight = Duration.between(now, LocalTime.MAX).plusSeconds(1)
            NextPrayerInfo(
                prayer = Prayer.FAJR,
                time = tomorrowFajr,
                remaining = untilMidnight.plus(Duration.between(LocalTime.MIN, tomorrowFajr)),
                isTomorrow = true
            )
        }
    }

    fun hasPassed(prayer: Prayer, now: LocalTime, times: Map<Prayer, LocalTime>): Boolean =
        !times.getValue(prayer).isAfter(now)
}

/** Formats a duration as `H:MM:SS`, or `MM:SS` when under an hour. */
fun Duration.toCountdownString(): String {
    val total = coerceAtLeast(Duration.ZERO).seconds
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/** Human-readable French summary, e.g. "dans 2 h 14". */
fun Duration.toFrenchRemaining(): String {
    val total = coerceAtLeast(Duration.ZERO).seconds
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    return when {
        hours > 0 -> "dans ${hours} h ${String.format("%02d", minutes)}"
        minutes > 0 -> "dans $minutes min"
        else -> "dans moins d'une minute"
    }
}
