package com.example.mysalat

import com.example.mysalat.data.CityCatalog
import com.example.mysalat.data.Prayer
import com.example.mysalat.data.PrayerTimesCalculator
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class PrayerTimesCalculatorTest {

    @Test
    fun dakarAndParisHaveDifferentMaghrib() {
        val date = LocalDate.of(2026, 8, 2)
        val dakar = PrayerTimesCalculator.timesFor(CityCatalog.byId("dakar"), date)
        val paris = PrayerTimesCalculator.timesFor(CityCatalog.byId("paris"), date)

        assertNotEquals(dakar[Prayer.MAGHRIB], paris[Prayer.MAGHRIB])
        assertTrue(dakar[Prayer.FAJR]!! < LocalTime.of(8, 0))
        assertTrue(paris[Prayer.MAGHRIB]!!.hour >= 20)
    }
}
