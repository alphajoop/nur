package com.example.mysalat.data

/**
 * One calendar day's archived prayer completions.
 * [mask] bits follow [Prayer.entries] order (bit 0 = Fajr … bit 4 = Isha).
 */
data class DayHistoryRecord(
    val date: String,
    val mask: Int
) {
    val completedCount: Int get() = Integer.bitCount(mask and 0b11111)
    val allCompleted: Boolean get() = completedCount == Prayer.COUNT

    fun isPrayerCompleted(prayer: Prayer): Boolean {
        val bit = Prayer.entries.indexOf(prayer)
        return bit >= 0 && (mask and (1 shl bit)) != 0
    }

    companion object {
        fun fromCompletions(date: String, completions: Map<Prayer, Boolean>): DayHistoryRecord {
            var mask = 0
            Prayer.entries.forEachIndexed { index, prayer ->
                if (completions[prayer] == true) {
                    mask = mask or (1 shl index)
                }
            }
            return DayHistoryRecord(date = date, mask = mask)
        }

        /** Compact wire format: `YYYY-MM-DD:mask|YYYY-MM-DD:mask|...` */
        fun encodeList(records: List<DayHistoryRecord>): String =
            records.joinToString("|") { "${it.date}:${it.mask}" }

        fun decodeList(raw: String): List<DayHistoryRecord> {
            if (raw.isBlank()) return emptyList()
            return raw.split("|")
                .mapNotNull { entry ->
                    val parts = entry.split(":")
                    if (parts.size != 2) return@mapNotNull null
                    val date = parts[0]
                    val mask = parts[1].toIntOrNull() ?: return@mapNotNull null
                    if (date.isBlank()) return@mapNotNull null
                    DayHistoryRecord(date = date, mask = mask.coerceIn(0, 0b11111))
                }
                .sortedByDescending { it.date }
        }
    }
}

/**
 * Aggregated stats over a calendar window (typically last 30 days).
 */
data class StatsSummary(
    val windowDays: Int = 30,
    val completeDays: Int = 0,
    val prayerCompletionPercent: Int = 0,
    val currentStreak: Int = 0,
    val trackedDays: Int = 0
)
