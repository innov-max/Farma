package com.example.mkulifarm.data

data class WeatherData(
    val temperature: Int,
    val soilTemperature: Int,
    val humidity: Int,
    val windSpeed: Int,
    val precipitation: Int,
    val sunrise: String,
    val sunset: String
)
