package com.example.serversimulator

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast?current=temperature_2m")
    suspend fun currentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): WeatherDto
}

@Serializable
data class WeatherDto(
    val current: CurrentWeatherDto,
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("temperature_2m") val temperature: Double,
)