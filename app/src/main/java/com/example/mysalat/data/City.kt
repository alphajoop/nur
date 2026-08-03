package com.example.mysalat.data

/**
 * Offline city used as the coordinates + timezone source for Adhan prayer times.
 */
data class City(
    val id: String,
    val displayName: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneId: String
)

object CityCatalog {

    val dakar = City(
        id = "dakar",
        displayName = "Dakar",
        latitude = 14.6928,
        longitude = -17.4467,
        timeZoneId = "Africa/Dakar"
    )

    val cities: List<City> = listOf(
        dakar,
        City("thies", "Thiès", 14.7886, -16.9260, "Africa/Dakar"),
        City("saint_louis", "Saint-Louis", 16.0326, -16.4819, "Africa/Dakar"),
        City("paris", "Paris", 48.8566, 2.3522, "Europe/Paris"),
        City("lyon", "Lyon", 45.7640, 4.8357, "Europe/Paris"),
        City("marseille", "Marseille", 43.2965, 5.3698, "Europe/Paris"),
        City("bruxelles", "Bruxelles", 50.8503, 4.3517, "Europe/Brussels"),
        City("casablanca", "Casablanca", 33.5731, -7.5898, "Africa/Casablanca"),
        City("alger", "Alger", 36.7538, 3.0588, "Africa/Algiers"),
        City("tunis", "Tunis", 36.8065, 10.1815, "Africa/Tunis"),
        City("istanbul", "Istanbul", 41.0082, 28.9784, "Europe/Istanbul"),
        City("makkah", "La Mecque", 21.3891, 39.8579, "Asia/Riyadh")
    )

    val default: City = dakar

    private val byIdMap = cities.associateBy { it.id }

    fun byId(id: String): City = byIdMap[id] ?: default
}
