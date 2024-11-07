package com.example.mkulifarm.data.Weather_data

data class HourlyWeatherResponse(
    val list: List<HourlyWeather>,
    val city: City
)

data class HourlyWeather(
    val dt: Long,  // Timestamp
    val main: MainWeatherData,
    val weather: List<WeatherDescription>,
    val wind: WindData
)

data class MainWeatherData(
    val temp: Double, // Temperature in Kelvin, we can convert this to Celsius or Fahrenheit
    val humidity: Int
)

data class WeatherDescription(
    val description: String
)

data class WindData(
    val speed: Double // Wind speed in m/s
)

data class City(
    val name: String,
    val country: String
)
