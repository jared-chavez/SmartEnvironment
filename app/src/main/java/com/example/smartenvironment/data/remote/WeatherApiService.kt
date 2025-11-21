package com.example.smartenvironment.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Data Models (para parsear la respuesta JSON de la API)

data class WeatherResponse(
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("main") val main: Main
)

data class Weather(
    @SerializedName("main") val main: String, // Ej: "Clouds", "Clear", "Rain"
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String // Ej: "01d", "10n"
)

data class Main(
    @SerializedName("temp") val temp: Double
)

// 2. Retrofit Service Interface

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // Para obtener la temperatura en Celsius
        @Query("lang") lang: String = "es"      // Para obtener la descripción en español
    ): WeatherResponse

    // 3. Companion object para crear una instancia única de Retrofit (Singleton)
    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/"

        fun create(): WeatherApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(WeatherApiService::class.java)
        }
    }
}
