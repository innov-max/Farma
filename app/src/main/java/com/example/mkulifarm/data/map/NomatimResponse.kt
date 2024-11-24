package com.example.mkulifarm.data.map

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.body
import io.ktor.client.plugins.json.JsonPlugin

import io.ktor.client.plugins.kotlinx.serializer.KotlinxSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Serializable
data class NominatimResponse(
    val lat: String,
    val lon: String,
    val display_name: String
)

object NominatimApi {

    private val client = HttpClient {
        install(JsonPlugin) {
            serializer = KotlinxSerializer() // JSON serializer
        }
    }

    suspend fun searchPlace(query: String): List<NominatimResponse> {
        return withContext(Dispatchers.IO) {
            // Make the HTTP request to the Nominatim API and parse the response to List<NominatimResponse>
            val response: List<NominatimResponse> = client.get("https://nominatim.openstreetmap.org/search") {
                parameter("q", query)
                parameter("format", "json")
                parameter("addressdetails", 1)
            }.body() // Deserialize the body directly into a List<NominatimResponse>

            return@withContext response
        }
    }
}
