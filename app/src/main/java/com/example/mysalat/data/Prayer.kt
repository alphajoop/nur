package com.example.mysalat.data

/**
 * The five daily obligatory prayers tracked by Nur.
 */
enum class Prayer(val displayName: String) {
    FAJR("Fajr"),
    DHUHR("Dhuhr"),
    ASR("Asr"),
    MAGHRIB("Maghrib"),
    ISHA("Isha");

    companion object {
        val COUNT = entries.size
    }
}
