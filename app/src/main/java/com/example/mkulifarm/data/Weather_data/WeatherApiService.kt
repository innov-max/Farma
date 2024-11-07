package com.example.mkulifarm.data.Weather_data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>
}




