package com.example.mysalat.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mysalat.DayPart
import com.example.mysalat.HomeUiState
import com.example.mysalat.PrayerRowState
import com.example.mysalat.data.Prayer
import com.example.mysalat.data.PrayerSchedule
import com.example.mysalat.data.VerseLibrary
import com.example.mysalat.ui.components.SectionHeader
import com.example.mysalat.ui.theme.MySalatTheme
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.Spacing
import java.time.LocalTime

/**
 * The tracking home screen. One vertical scroll, sections separated by
 * consistent spacing, hero animating in once on first composition.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onPrayerToggled: (Prayer, Boolean) -> Unit,
    onOpenProfile: () -> Unit,
    onQuickAction: (QuickAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var heroVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { heroVisible = true }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        GreetingHeader(state = state, onAvatarClick = onOpenProfile)

        AnimatedVisibility(
            visible = heroVisible,
            enter = fadeIn(Motion.fadeMedium()) +
                slideInVertically(Motion.springGentle()) { height -> height / 4 }
        ) {
            HeroCard(state = state)
        }

        StreakCard(streakCount = state.streakCount)

        ProgressCard(
            completedCount = state.completedCount,
            progress = state.progress,
            encouragement = state.encouragement
        )

        SectionHeader(title = "Prières du jour")

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            state.rows.forEach { row ->
                PrayerCard(
                    row = row,
                    onToggle = { completed -> onPrayerToggled(row.prayer, completed) }
                )
            }
        }

        VerseCard(verse = state.verse)

        SectionHeader(title = "Raccourcis")

        QuickActionsGrid(onActionClick = onQuickAction)
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

private fun previewState(completed: Int): HomeUiState {
    val now = LocalTime.of(11, 12)
    return HomeUiState(
        userName = "Ahmed",
        dayPart = DayPart.of(now),
        formattedDate = "samedi 1 août 2026",
        next = PrayerSchedule.nextPrayer(now),
        rows = Prayer.entries.mapIndexed { index, prayer ->
            PrayerRowState(
                prayer = prayer,
                time = PrayerSchedule.formattedTime(prayer),
                completed = index < completed,
                isNext = PrayerSchedule.nextPrayer(now).prayer == prayer,
                hasPassed = PrayerSchedule.hasPassed(prayer, now)
            )
        },
        completedCount = completed,
        streakCount = 7,
        verse = VerseLibrary.first
    )
}

@Preview(name = "Accueil clair", heightDp = 1500)
@Composable
private fun HomeScreenLightPreview() {
    MySalatTheme(darkTheme = false) {
        HomeScreen(
            state = previewState(completed = 2),
            onPrayerToggled = { _, _ -> },
            onOpenProfile = {},
            onQuickAction = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}

@Preview(name = "Accueil sombre", heightDp = 1500)
@Composable
private fun HomeScreenDarkPreview() {
    MySalatTheme(darkTheme = true) {
        HomeScreen(
            state = previewState(completed = 5),
            onPrayerToggled = { _, _ -> },
            onOpenProfile = {},
            onQuickAction = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}
