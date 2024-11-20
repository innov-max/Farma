package com.example.mkulifarm.data.BasicDataHandling

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Esp8266Api {
    @GET("led1/{action}")
    fun toggleLED1(@Path("action") action: String): Call<Void>

    @GET("led2/{action}")
    fun toggleLED2(@Path("action") action: String): Call<Void>

    @GET("led3/{action}")
    fun toggleLED3(@Path("action") action: String): Call<Void>
}

