package com.example.mkulifarm.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
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
        // Set initial location
        val initialLatitude = -1.3088851164172204
        val initialLongitude = 36.81208148298125
        val initialLocation = getLocationName(initialLatitude, initialLongitude)
        _location.value = initialLocation // Set the location to the initial one

        // Start periodic updates
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
                // Making the API call
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
                            soilTemperature = it.main.temp_min.toInt(), // Corrected property name
                            humidity = it.main.humidity,
                            windSpeed = it.wind.speed.toInt(),
                            precipitation = 0, // Placeholder
                            sunrise = sunriseTime,
                            sunset = sunsetTime
                        )
                        Log.d("WeatherViewModel", "Weather data fetched successfully")

                        // Set location name (You can improve this if you want more details)
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
        // For simplicity, this is a placeholder method. You can use reverse geocoding to get the location name.
        return "Latitude: $latitude, Longitude: $longitude"
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        return format.format(date)
    }
}
