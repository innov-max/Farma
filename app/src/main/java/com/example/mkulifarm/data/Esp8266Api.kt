package com.example.mkulifarm.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Esp8266Api {

    @GET("led/{action}")
    fun toggleLED(@Path("action") action: String): Call<Void>
}