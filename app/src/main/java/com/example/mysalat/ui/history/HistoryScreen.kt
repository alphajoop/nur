package com.example.mysalat.ui.history

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mysalat.data.DayHistoryRecord
import com.example.mysalat.data.Prayer
import com.example.mysalat.data.StatsSummary
import com.example.mysalat.toFrenchShortDate
import com.example.mysalat.ui.components.GlassCard
import com.example.mysalat.ui.components.IconBadge
import com.example.mysalat.ui.components.ProgressRing
import com.example.mysalat.ui.components.SectionHeader
import com.example.mysalat.ui.icons.AppIcons
import com.example.mysalat.ui.icons.IconSize
import com.example.mysalat.ui.theme.Elevation
import com.example.mysalat.ui.theme.Motion
import com.example.mysalat.ui.theme.MySalatTheme
import com.example.mysalat.ui.theme.Radius
import com.example.mysalat.ui.theme.Spacing
import com.example.mysalat.ui.theme.brand
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val DOTS_PER_ROW = 10

/**
 * Historical view: a 30-day summary ring, a day-by-day heat strip, and detailed
 * cards for the most recent days.
 */
@Composable
fun HistoryScreen(
    summary: StatsSummary,
    history: List<DayHistoryRecord>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val byDate = remember(history) { history.associateBy { it.date } }
    val strip = remember(history, summary.windowDays) {
        buildStrip(summary.windowDays, byDate)
    }
    val recent = remember(history) { history.take(14) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(
                text = "Historique",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Vos ${summary.windowDays} derniers jours",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SummaryCard(summary = summary)
        HeatStripCard(days = strip)

        SectionHeader(title = "Détail des jours")

        if (recent.isEmpty()) {
            EmptyHistoryCard()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                recent.forEach { record -> DayCard(record = record) }
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: StatsSummary, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressRing(
                    progress = summary.prayerCompletionPercent / 100f,
                    diameter = 108.dp,
                    strokeWidth = 10.dp
                ) {
                    Text(
                        text = "${summary.prayerCompletionPercent} %",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "assiduité",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    StatLine(
                        icon = AppIcons.Flame,
                        value = summary.currentStreak.toString(),
                        label = if (summary.currentStreak == 1) "jour de série" else "jours de série"
                    )
                    StatLine(
                        icon = AppIcons.CheckAll,
                        value = summary.completeDays.toString(),
                        label = "journées complètes"
                    )
                    StatLine(
                        icon = AppIcons.Clock,
                        value = summary.trackedDays.toString(),
                        label = "jours suivis"
                    )
                }
            }
        }
    }
}

@Composable
private fun StatLine(
    icon: Int,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBadge(icon = icon, size = 32.dp, iconSize = IconSize.sm, cornerRadius = Radius.sm)
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** One dot per day, tinted by how many prayers were completed. */
private data class StripDay(
    val date: LocalDate,
    val completedCount: Int
)

@Composable
private fun HeatStripCard(days: List<StripDay>, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = "Vue d'ensemble",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Rows of ten keep the whole window visible without horizontal scroll.
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                days.chunked(DOTS_PER_ROW).forEach { rowDays ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        rowDays.forEach { day ->
                            DayDot(day = day, modifier = Modifier.weight(1f))
                        }
                        repeat(DOTS_PER_ROW - rowDays.size) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Legend()
        }
    }
}

@Composable
private fun DayDot(day: StripDay, modifier: Modifier = Modifier) {
    val brand = MaterialTheme.brand
    val colorScheme = MaterialTheme.colorScheme
    val ratio = day.completedCount / Prayer.COUNT.toFloat()

    val fill = when {
        day.completedCount == 0 -> brand.ringTrack
        day.completedCount == Prayer.COUNT -> colorScheme.primary
        else -> colorScheme.primary.copy(alpha = 0.25f + 0.55f * ratio)
    }
    val pop by animateFloatAsState(
        targetValue = if (day.completedCount > 0) 1f else 0.86f,
        animationSpec = Motion.springBouncy(),
        label = "dotPop"
    )

    Box(
        modifier = modifier
            .height(22.dp)
            .scale(pop)
            .clip(RoundedCornerShape(Radius.sm))
            .background(fill)
            .semantics {
                contentDescription =
                    "${day.date.toFrenchShortDate()} : ${day.completedCount} sur ${Prayer.COUNT}"
            }
    )
}

@Composable
private fun Legend(modifier: Modifier = Modifier) {
    val brand = MaterialTheme.brand
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "0",
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant
        )
        listOf(
            brand.ringTrack,
            colorScheme.primary.copy(alpha = 0.35f),
            colorScheme.primary.copy(alpha = 0.6f),
            colorScheme.primary
        ).forEach { color ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        Text(
            text = "${Prayer.COUNT}",
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DayCard(record: DayHistoryRecord, modifier: Modifier = Modifier) {
    val brand = MaterialTheme.brand
    val colorScheme = MaterialTheme.colorScheme
    val date = remember(record.date) {
        runCatching { LocalDate.parse(record.date, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
    }
    val label = date?.toFrenchShortDate() ?: record.date

    GlassCard(modifier = modifier.fillMaxWidth(), elevation = Elevation.card) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "${record.completedCount} / ${Prayer.COUNT} prières",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Prayer.entries.forEach { prayer ->
                    val done = record.isPrayerCompleted(prayer)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (done) colorScheme.primary else Color.Transparent)
                            .border(
                                width = Elevation.hairline,
                                color = if (done) Color.Transparent else colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                }
            }

            if (record.allCompleted) {
                IconBadge(
                    icon = AppIcons.Star,
                    containerColor = brand.goldWash,
                    tint = brand.gold,
                    size = 32.dp,
                    iconSize = IconSize.sm,
                    cornerRadius = Radius.sm
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard(modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            IconBadge(icon = AppIcons.History, size = 48.dp)
            Text(
                text = "Aucun historique pour le moment",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Cochez vos prières du jour, votre progression apparaîtra ici.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun buildStrip(
    windowDays: Int,
    byDate: Map<String, DayHistoryRecord>
): List<StripDay> {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val end = LocalDate.now()
    return (windowDays - 1 downTo 0).map { offset ->
        val date = end.minusDays(offset.toLong())
        StripDay(
            date = date,
            completedCount = byDate[date.format(formatter)]?.completedCount ?: 0
        )
    }
}

@Preview(name = "Historique clair", heightDp = 1200)
@Composable
private fun HistoryLightPreview() {
    MySalatTheme(darkTheme = false) {
        HistoryScreen(
            summary = StatsSummary(
                completeDays = 12,
                prayerCompletionPercent = 74,
                currentStreak = 5,
                trackedDays = 21
            ),
            history = previewHistory(),
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}

@Preview(name = "Historique sombre", heightDp = 1200)
@Composable
private fun HistoryDarkPreview() {
    MySalatTheme(darkTheme = true) {
        HistoryScreen(
            summary = StatsSummary(
                completeDays = 3,
                prayerCompletionPercent = 41,
                currentStreak = 1,
                trackedDays = 9
            ),
            history = previewHistory(),
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = Spacing.md)
        )
    }
}

private fun previewHistory(): List<DayHistoryRecord> {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val masks = listOf(0b11111, 0b01111, 0b11111, 0b00111, 0b11011, 0b00001, 0b11111)
    return masks.mapIndexed { index, mask ->
        DayHistoryRecord(
            date = LocalDate.now().minusDays(index.toLong()).format(formatter),
            mask = mask
        )
    }
}
