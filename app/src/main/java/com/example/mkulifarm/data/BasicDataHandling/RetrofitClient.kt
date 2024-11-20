package com.example.mkulifarm.data.BasicDataHandling
import com.example.mkulifarm.data.Weather_data.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherRetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val instance: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
