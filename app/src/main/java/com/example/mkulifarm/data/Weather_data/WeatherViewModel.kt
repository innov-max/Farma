package com.example.mkulifarm.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.mkulifarm.data.Weather_data.WeatherData
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class WeatherViewModel : ViewModel() {
    private val apiKey = "7803a06dbea38d09361cf4ba9d5be8a6" // Replace with your actual API key

    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    init {
        // Set initial location (latitude and longitude)
        val initialLatitude = -1.3088851164172204
        val initialLongitude = 36.81208148298125
        val initialLocation = getLocationName(initialLatitude, initialLongitude)
        _location.value = initialLocation // Set the location to the initial one

        // Fetch weather data initially and start periodic updates
        fetchWeatherData(initialLatitude, initialLongitude)
        startPeriodicWeatherUpdates(latitude = initialLatitude, longitude = initialLongitude)
    }

    private fun startPeriodicWeatherUpdates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            // Run the weather fetch every 5 minutes (300,000 ms)
            while (true) {
                fetchWeatherData(latitude, longitude)
                delay(5 * 60 * 1000) // 5 minutes in milliseconds
            }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                // Making the API call to the OpenWeatherMap API
                val response = WeatherRetrofitClient.instance.getWeatherData(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey = apiKey
                )

                if (response.isSuccessful) {
                    response.body()?.let {
                        val sunriseTime = formatTime(it.sys.sunrise)
                        val sunsetTime = formatTime(it.sys.sunset)

                        // Update weather data
                        _weatherData.value = WeatherData(
                            temperature = it.main.temp.toInt(),
                            soilTemperature = it.main.temp_min.toInt(), // Can be used for soil temp if needed
                            humidity = it.main.humidity,
                            windSpeed = it.wind.speed.toInt(),
                            precipitation = 0, // Placeholder (you can update it based on API response if available)
                            sunrise = sunriseTime,
                            sunset = sunsetTime
                        )

                        // Set location name
                        val locationName = getLocationName(latitude, longitude)
                        _location.value = locationName
                    }
                } else {
                    Log.e("WeatherViewModel", "Error: ${response.code()} - ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Exception: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    private fun getLocationName(latitude: Double, longitude: Double): String {
        // For simplicity, you can use reverse geocoding here if you want a location name,
        // or just return the latitude/longitude as a string.
        return "Latitude: $latitude, Longitude: $longitude"
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        return format.format(date)
    }
}

