package com.example.mkulifarm.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    fun getEverything(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String = "en"
    ): Call<NewsResponse>
}
