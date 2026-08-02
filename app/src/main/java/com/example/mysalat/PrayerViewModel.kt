package com.example.mysalat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysalat.data.DayHistoryRecord
import com.example.mysalat.data.Prayer
import com.example.mysalat.data.PrayerDayState
import com.example.mysalat.data.PrayerSchedule
import com.example.mysalat.data.PrayerStorage
import com.example.mysalat.data.StatsSummary
import com.example.mysalat.data.VerseLibrary
import com.example.mysalat.ui.navigation.AppDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Owns prayer tracking state, streak rules, derived history/stats, the clock
 * ticker that drives the countdown, and top-level navigation.
 */
class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = PrayerStorage(application.applicationContext)

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val _destination = MutableStateFlow(AppDestination.Home)
    val destination: StateFlow<AppDestination> = _destination.asStateFlow()

    /** Ticks once per second so the hero countdown stays live. */
    private val _now = MutableStateFlow(LocalTime.now().withNano(0))
    val now: StateFlow<LocalTime> = _now.asStateFlow()

    val uiState: StateFlow<PrayerDayState> = storage.prayerDayState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PrayerDayState()
        )

    val history: StateFlow<List<DayHistoryRecord>> = storage.historyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val userName: StateFlow<String> = storage.userNameFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PrayerStorage.DEFAULT_USER_NAME
        )

    /**
     * Everything the home screen needs, recombined whenever the clock ticks,
     * completions change, or the user renames themselves.
     */
    val homeState: StateFlow<HomeUiState> =
        combine(uiState, now, userName) { day, currentTime, name ->
            buildHomeState(day, currentTime, name)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    /**
     * Merges live today into archived history for display, then derives a 30-day summary.
     */
    val statsSummary: StateFlow<StatsSummary> = combine(uiState, history) { today, archived ->
        buildStatsSummary(today, archived)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsSummary()
    )

    /**
     * History list with today's live completions preferred over any archived row for today.
     */
    val displayHistory: StateFlow<List<DayHistoryRecord>> = combine(uiState, history) { today, archived ->
        mergeTodayIntoHistory(today, archived)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            processDailyLifecycle()
        }
        viewModelScope.launch {
            while (true) {
                _now.value = LocalTime.now().withNano(0)
                delay(1_000)
            }
        }
    }

    fun navigateTo(destination: AppDestination) {
        _destination.value = destination
    }

    /** Returns false when already on Home, letting the caller finish the activity. */
    fun navigateBack(): Boolean {
        if (_destination.value == AppDestination.Home) return false
        _destination.value = AppDestination.Home
        return true
    }

    fun onPrayerToggled(prayer: Prayer, completed: Boolean) {
        viewModelScope.launch {
            val today = LocalDate.now().format(dateFormatter)
            storage.setPrayerCompleted(prayer, completed, today)
        }
    }

    fun onUserNameChanged(name: String) {
        viewModelScope.launch {
            storage.setUserName(name)
        }
    }

    private fun buildHomeState(
        day: PrayerDayState,
        currentTime: LocalTime,
        name: String
    ): HomeUiState {
        val next = PrayerSchedule.nextPrayer(currentTime)
        val rows = Prayer.entries.map { prayer ->
            PrayerRowState(
                prayer = prayer,
                time = PrayerSchedule.formattedTime(prayer),
                completed = day.completions[prayer] == true,
                isNext = !next.isTomorrow && next.prayer == prayer,
                hasPassed = PrayerSchedule.hasPassed(prayer, currentTime)
            )
        }
        val today = LocalDate.now()

        return HomeUiState(
            userName = name,
            dayPart = DayPart.of(currentTime),
            formattedDate = today.toFrenchLongDate(),
            next = next,
            rows = rows,
            completedCount = day.completedCount,
            streakCount = day.streakCount,
            verse = VerseLibrary.forDate(today)
        )
    }

    private suspend fun processDailyLifecycle() {
        val today = LocalDate.now()
        val todayString = today.format(dateFormatter)
        val current = storage.prayerDayState.first()

        if (current.lastCompletedDate.isEmpty()) {
            storage.initializeTrackingDate(todayString)
            return
        }

        if (current.lastCompletedDate == todayString) {
            reconcileSameDayStreak(current, todayString)
            return
        }

        val lastDate = runCatching {
            LocalDate.parse(current.lastCompletedDate, dateFormatter)
        }.getOrNull()

        val daysBetween = lastDate?.let { ChronoUnit.DAYS.between(it, today) }

        val newStreak = when {
            daysBetween == null -> 0
            daysBetween == 1L && current.allCompleted -> {
                if (current.isStreakAwardedFor(current.lastCompletedDate)) {
                    current.streakCount
                } else {
                    current.streakCount + 1
                }
            }
            else -> 0
        }

        storage.startNewDay(
            today = todayString,
            newStreak = newStreak,
            previousDate = current.lastCompletedDate,
            previousCompletions = current.completions
        )
    }

    private suspend fun reconcileSameDayStreak(current: PrayerDayState, today: String) {
        if (current.allCompleted && !current.isStreakAwardedFor(today)) {
            storage.awardStreakIfNeeded(today)
        } else if (current.completedCount > 0) {
            storage.upsertDay(today, current.completions)
        }
    }

    private fun mergeTodayIntoHistory(
        today: PrayerDayState,
        archived: List<DayHistoryRecord>
    ): List<DayHistoryRecord> {
        val todayDate = today.lastCompletedDate.ifBlank {
            LocalDate.now().format(dateFormatter)
        }
        val live = DayHistoryRecord.fromCompletions(todayDate, today.completions)
        val withoutToday = archived.filterNot { it.date == todayDate }
        return if (live.completedCount > 0) {
            (withoutToday + live).sortedByDescending { it.date }
        } else {
            withoutToday.sortedByDescending { it.date }
        }
    }

    private fun buildStatsSummary(
        today: PrayerDayState,
        archived: List<DayHistoryRecord>
    ): StatsSummary {
        val windowDays = 30
        val end = LocalDate.now()
        val start = end.minusDays((windowDays - 1).toLong())
        val merged = mergeTodayIntoHistory(today, archived).associateBy { it.date }

        var completeDays = 0
        var prayerSum = 0
        var trackedDays = 0

        var cursor = start
        while (!cursor.isAfter(end)) {
            val key = cursor.format(dateFormatter)
            val record = merged[key]
            if (record != null) {
                trackedDays++
                prayerSum += record.completedCount
                if (record.allCompleted) completeDays++
            }
            cursor = cursor.plusDays(1)
        }

        val percent = ((prayerSum * 100f) / (windowDays * Prayer.COUNT)).toInt()

        return StatsSummary(
            windowDays = windowDays,
            completeDays = completeDays,
            prayerCompletionPercent = percent.coerceIn(0, 100),
            currentStreak = today.streakCount,
            trackedDays = trackedDays
        )
    }
}
