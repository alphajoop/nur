package com.example.mysalat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.prayerDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "prayer_preferences"
)

/**
 * Snapshot of all persisted prayer-tracking state for a given calendar day.
 */
data class PrayerDayState(
    val completions: Map<Prayer, Boolean> = Prayer.entries.associateWith { false },
    val streakCount: Int = 0,
    /** Calendar day ("YYYY-MM-DD") that [completions] currently represent. */
    val lastCompletedDate: String = "",
    /** Day for which [streakCount] already includes today's completion bonus. */
    val streakAwardedDate: String = ""
) {
    val completedCount: Int get() = completions.values.count { it }
    val allCompleted: Boolean get() = completedCount == Prayer.COUNT
    val progress: Float get() = completedCount / Prayer.COUNT.toFloat()

    fun isStreakAwardedFor(date: String): Boolean =
        streakAwardedDate.isNotEmpty() && streakAwardedDate == date
}

/**
 * Preferences DataStore persistence for daily prayer checkboxes, streak, and history.
 */
class PrayerStorage(private val context: Context) {

    private object Keys {
        val FAJR = booleanPreferencesKey("fajr")
        val DHUHR = booleanPreferencesKey("dhuhr")
        val ASR = booleanPreferencesKey("asr")
        val MAGHRIB = booleanPreferencesKey("maghrib")
        val ISHA = booleanPreferencesKey("isha")
        val STREAK_COUNT = intPreferencesKey("streak_count")
        val LAST_COMPLETED_DATE = stringPreferencesKey("last_completed_date")
        val STREAK_AWARDED_DATE = stringPreferencesKey("streak_awarded_date")
        val HISTORY = stringPreferencesKey("history")
        val USER_NAME = stringPreferencesKey("user_name")
        val CITY_ID = stringPreferencesKey("city_id")

        fun forPrayer(prayer: Prayer): Preferences.Key<Boolean> = when (prayer) {
            Prayer.FAJR -> FAJR
            Prayer.DHUHR -> DHUHR
            Prayer.ASR -> ASR
            Prayer.MAGHRIB -> MAGHRIB
            Prayer.ISHA -> ISHA
        }
    }

    val prayerDayState: Flow<PrayerDayState> = context.prayerDataStore.data.map { prefs ->
        PrayerDayState(
            completions = Prayer.entries.associateWith { prayer ->
                prefs[Keys.forPrayer(prayer)] ?: false
            },
            streakCount = prefs[Keys.STREAK_COUNT] ?: 0,
            lastCompletedDate = prefs[Keys.LAST_COMPLETED_DATE] ?: "",
            streakAwardedDate = prefs[Keys.STREAK_AWARDED_DATE] ?: ""
        )
    }

    val historyFlow: Flow<List<DayHistoryRecord>> = context.prayerDataStore.data.map { prefs ->
        DayHistoryRecord.decodeList(prefs[Keys.HISTORY] ?: "")
    }

    val userNameFlow: Flow<String> = context.prayerDataStore.data.map { prefs ->
        prefs[Keys.USER_NAME]?.takeIf { it.isNotBlank() } ?: DEFAULT_USER_NAME
    }

    val cityIdFlow: Flow<String> = context.prayerDataStore.data.map { prefs ->
        prefs[Keys.CITY_ID]?.takeIf { it.isNotBlank() } ?: CityCatalog.default.id
    }

    suspend fun setUserName(name: String) {
        val cleaned = name.trim().take(MAX_USER_NAME_LENGTH)
        context.prayerDataStore.edit { prefs ->
            if (cleaned.isEmpty()) {
                prefs.remove(Keys.USER_NAME)
            } else {
                prefs[Keys.USER_NAME] = cleaned
            }
        }
    }

    suspend fun setCityId(cityId: String) {
        val resolved = CityCatalog.byId(cityId).id
        context.prayerDataStore.edit { prefs ->
            prefs[Keys.CITY_ID] = resolved
        }
    }

    /**
     * Updates a prayer checkbox, upserts today's history row, and awards/revokes streak:
     * - 5/5 and not yet awarded for [today] → streak + 1
     * - drops below 5/5 after an award today → streak - 1 (floor 0)
     */
    suspend fun setPrayerCompleted(prayer: Prayer, completed: Boolean, today: String) {
        context.prayerDataStore.edit { prefs ->
            prefs[Keys.forPrayer(prayer)] = completed

            val completions = Prayer.entries.associateWith { entry ->
                if (entry == prayer) completed else (prefs[Keys.forPrayer(entry)] ?: false)
            }
            val allDone = completions.values.all { it }
            val awardedDate = prefs[Keys.STREAK_AWARDED_DATE] ?: ""
            val currentStreak = prefs[Keys.STREAK_COUNT] ?: 0

            when {
                allDone && awardedDate != today -> {
                    prefs[Keys.STREAK_COUNT] = currentStreak + 1
                    prefs[Keys.STREAK_AWARDED_DATE] = today
                }
                !allDone && awardedDate == today -> {
                    prefs[Keys.STREAK_COUNT] = (currentStreak - 1).coerceAtLeast(0)
                    prefs.remove(Keys.STREAK_AWARDED_DATE)
                }
            }

            writeHistoryRecord(
                prefs = prefs,
                record = DayHistoryRecord.fromCompletions(today, completions)
            )
        }
    }

    suspend fun upsertDay(date: String, completions: Map<Prayer, Boolean>) {
        context.prayerDataStore.edit { prefs ->
            writeHistoryRecord(
                prefs = prefs,
                record = DayHistoryRecord.fromCompletions(date, completions)
            )
        }
    }

    /**
     * Awards today's streak if all five prayers are complete and the day
     * has not yet been credited (idempotent migration helper).
     */
    suspend fun awardStreakIfNeeded(today: String) {
        context.prayerDataStore.edit { prefs ->
            val completions = Prayer.entries.associateWith { prefs[Keys.forPrayer(it)] == true }
            val allDone = completions.values.all { it }
            val awardedDate = prefs[Keys.STREAK_AWARDED_DATE] ?: ""
            if (allDone && awardedDate != today) {
                val currentStreak = prefs[Keys.STREAK_COUNT] ?: 0
                prefs[Keys.STREAK_COUNT] = currentStreak + 1
                prefs[Keys.STREAK_AWARDED_DATE] = today
            }
            if (completions.values.any { it }) {
                writeHistoryRecord(
                    prefs = prefs,
                    record = DayHistoryRecord.fromCompletions(today, completions)
                )
            }
        }
    }

    /**
     * Archives the previous tracking day's completions, then clears checkboxes,
     * sets streak, stamps [today], and clears today's award flag.
     */
    suspend fun startNewDay(
        today: String,
        newStreak: Int,
        previousDate: String,
        previousCompletions: Map<Prayer, Boolean>
    ) {
        context.prayerDataStore.edit { prefs ->
            if (previousDate.isNotBlank()) {
                writeHistoryRecord(
                    prefs = prefs,
                    record = DayHistoryRecord.fromCompletions(previousDate, previousCompletions)
                )
            }
            Prayer.entries.forEach { prayer ->
                prefs[Keys.forPrayer(prayer)] = false
            }
            prefs[Keys.STREAK_COUNT] = newStreak
            prefs[Keys.LAST_COMPLETED_DATE] = today
            prefs.remove(Keys.STREAK_AWARDED_DATE)
        }
    }

    /** First launch: stamp today so subsequent opens can detect day changes. */
    suspend fun initializeTrackingDate(today: String) {
        context.prayerDataStore.edit { prefs ->
            prefs[Keys.LAST_COMPLETED_DATE] = today
        }
    }

    private fun writeHistoryRecord(prefs: MutablePreferences, record: DayHistoryRecord) {
        val existing = DayHistoryRecord.decodeList(prefs[Keys.HISTORY] ?: "")
            .filterNot { it.date == record.date }
        val updated = (existing + record).sortedByDescending { it.date }
        prefs[Keys.HISTORY] = DayHistoryRecord.encodeList(updated)
    }

    companion object {
        const val DEFAULT_USER_NAME = "Alpha"
        const val MAX_USER_NAME_LENGTH = 24
    }
}
