package com.example.mkulifarm.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.150/" // Replace with your ESP8266 IP

    val instance: Esp8266Api by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Esp8266Api::class.java)
    }
}
