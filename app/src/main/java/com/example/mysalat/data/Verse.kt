package com.example.mysalat.data

import java.time.LocalDate

/**
 * A short Qur'anic passage shown on the home screen.
 */
data class Verse(
    val arabic: String,
    val translation: String,
    val reference: String
)

/**
 * Static rotation of verses. One is surfaced per calendar day so the home
 * screen changes without any network call.
 */
object VerseLibrary {

    private val verses = listOf(
        Verse(
            arabic = "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا",
            translation = "La prière est pour les croyants une prescription à des heures déterminées.",
            reference = "An-Nisa, 4:103"
        ),
        Verse(
            arabic = "وَأَقِمِ الصَّلَاةَ إِنَّ الصَّلَاةَ تَنْهَى عَنِ الْفَحْشَاءِ وَالْمُنكَرِ",
            translation = "Accomplis la prière, car la prière préserve de la turpitude et du blâmable.",
            reference = "Al-Ankabut, 29:45"
        ),
        Verse(
            arabic = "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
            translation = "Souvenez-vous de Moi, Je Me souviendrai de vous. Soyez reconnaissants.",
            reference = "Al-Baqara, 2:152"
        ),
        Verse(
            arabic = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            translation = "En vérité, à côté de la difficulté est la facilité.",
            reference = "Ash-Sharh, 94:6"
        ),
        Verse(
            arabic = "وَاسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ",
            translation = "Cherchez secours dans la patience et la prière.",
            reference = "Al-Baqara, 2:45"
        ),
        Verse(
            arabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي",
            translation = "Seigneur, apaise mon cœur et facilite ma tâche.",
            reference = "Ta-Ha, 20:25-26"
        ),
        Verse(
            arabic = "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا",
            translation = "À quiconque craint Allah, Il donne une issue favorable.",
            reference = "At-Talaq, 65:2"
        )
    )

    /** Deterministic pick so the verse is stable throughout a given day. */
    fun forDate(date: LocalDate): Verse = verses[date.dayOfYear % verses.size]

    val first: Verse get() = verses.first()
}
