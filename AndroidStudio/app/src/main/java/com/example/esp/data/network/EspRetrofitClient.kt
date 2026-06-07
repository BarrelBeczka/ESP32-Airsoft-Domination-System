package com.example.esp.data.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object EspRetrofitClient {
    var baseUrl: String = "http://192.168.4.1/"

    private val jsonConfig = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    val api: EspApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create()) // for string responses
            .addConverterFactory(jsonConfig.asConverterFactory(contentType)) // for json responses
            .build()
            .create(EspApiService::class.java)
    }

    // Helper function to dynamically update the base URL and recreate the API instance
    // Note: Due to `by lazy`, we must manage recreation if URL changes at runtime.
    // However, for this simple implementation, we can adjust the client.
    
    private var _api: EspApiService? = null
    
    fun getDynamicApi(): EspApiService {
        if (_api == null) {
            _api = Retrofit.Builder()
                .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                .build()
                .create(EspApiService::class.java)
        }
        return _api!!
    }

    fun updateBaseUrl(newUrl: String) {
        val urlToSet = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (baseUrl != urlToSet) {
            baseUrl = urlToSet
            _api = null // Force recreation on next use
        }
    }
}
