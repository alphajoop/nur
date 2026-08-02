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
 * Fixed, display-only prayer times. The app intentionally does not compute
 * astronomical times or request location; these values simply give the home
 * screen a realistic schedule to count down against.
 */
object PrayerSchedule {

    private val times: Map<Prayer, LocalTime> = mapOf(
        Prayer.FAJR to LocalTime.of(5, 42),
        Prayer.DHUHR to LocalTime.of(13, 30),
        Prayer.ASR to LocalTime.of(17, 15),
        Prayer.MAGHRIB to LocalTime.of(21, 8),
        Prayer.ISHA to LocalTime.of(22, 45)
    )

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun timeFor(prayer: Prayer): LocalTime = times.getValue(prayer)

    fun formattedTime(prayer: Prayer): String = formatter.format(timeFor(prayer))

    val ordered: List<Pair<Prayer, LocalTime>> = Prayer.entries.map { it to times.getValue(it) }

    /**
     * Returns the upcoming prayer for [now], wrapping to tomorrow's Fajr once
     * Isha has passed.
     */
    fun nextPrayer(now: LocalTime): NextPrayerInfo {
        val upcoming = ordered.firstOrNull { (_, time) -> time.isAfter(now) }

        return if (upcoming != null) {
            NextPrayerInfo(
                prayer = upcoming.first,
                time = upcoming.second,
                remaining = Duration.between(now, upcoming.second),
                isTomorrow = false
            )
        } else {
            val fajr = timeFor(Prayer.FAJR)
            val untilMidnight = Duration.between(now, LocalTime.MAX).plusSeconds(1)
            NextPrayerInfo(
                prayer = Prayer.FAJR,
                time = fajr,
                remaining = untilMidnight.plus(Duration.between(LocalTime.MIN, fajr)),
                isTomorrow = true
            )
        }
    }

    /** True once a prayer's time has passed for the current day. */
    fun hasPassed(prayer: Prayer, now: LocalTime): Boolean = !timeFor(prayer).isAfter(now)
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
