package com.example.mkulifarm.data.Weather_data

// Example of how the response from OpenWeatherMap is mapped to these classes
data class WeatherData(
    val temperature: Int,
    val soilTemperature: Int,
    val humidity: Int,
    val windSpeed: Int,
    val precipitation: Int,
    val sunrise: String,
    val sunset: String
)
