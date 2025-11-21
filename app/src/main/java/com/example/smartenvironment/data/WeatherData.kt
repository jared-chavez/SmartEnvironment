package com.example.smartenvironment.data

data class WeatherData(
    val temperature: Int,
    val description: String,
    val iconType: WeatherIconType
)

enum class WeatherIconType {
    SUNNY,         // Sol
    CLOUDY,        // Nubes
    RAINY,         // Lluvia
    PARTLY_CLOUDY, // Parcialmente nublado
    NIGHT,         // Noche despejada
    UNKNOWN        // Desconocido
}
