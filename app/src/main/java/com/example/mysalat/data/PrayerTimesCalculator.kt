package com.example.mysalat.data

import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant as KotlinInstant

/**
 * Computes the five daily prayer times for a [City] and calendar day using Adhan
 * (Muslim World League + Shafi madhab). Fully offline.
 */
object PrayerTimesCalculator {

    @OptIn(ExperimentalTime::class)
    fun timesFor(city: City, date: LocalDate): Map<Prayer, LocalTime> {
        val zone = ZoneId.of(city.timeZoneId)
        val coordinates = Coordinates(city.latitude, city.longitude)
        val components = DateComponents(date.year, date.monthValue, date.dayOfMonth)
        val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters.copy(madhab = Madhab.SHAFI)
        val prayerTimes = PrayerTimes(coordinates, components, params)

        return mapOf(
            Prayer.FAJR to toLocalTime(prayerTimes.fajr, zone),
            Prayer.DHUHR to toLocalTime(prayerTimes.dhuhr, zone),
            Prayer.ASR to toLocalTime(prayerTimes.asr, zone),
            Prayer.MAGHRIB to toLocalTime(prayerTimes.maghrib, zone),
            Prayer.ISHA to toLocalTime(prayerTimes.isha, zone)
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun toLocalTime(instant: KotlinInstant, zone: ZoneId): LocalTime =
        Instant.ofEpochMilli(instant.toEpochMilliseconds())
            .atZone(zone)
            .toLocalTime()
            .withSecond(0)
            .withNano(0)
}
