package com.example.mkulifarm.data.Weather_data

data class WeatherResponse(
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val weather: List<Weather>
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class Wind(
    val speed: Double
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)

data class Weather(
    val description: String
)
