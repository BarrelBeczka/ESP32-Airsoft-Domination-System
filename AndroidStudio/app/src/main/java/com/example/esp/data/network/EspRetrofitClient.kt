package com.example.esp.data.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
// Konfiguracja klienta Retrofit Do komunikacji z ESP32
object EspRetrofitClient {
    // Domyślny adres ESP32
    var baseUrl: String = "http://192.168.4.1/"

    private val jsonConfig = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()
    // Statyczna instancja API
    val api: EspApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .build()
            .create(EspApiService::class.java)
    }

    // Dynamiczna instancja API
    private var _api: EspApiService? = null

    // Pobieranie instancji APi z Aktualnym adresem IP
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

    // Zmiana IP ESP32 gdy użytkownik zmieni ip
    fun updateBaseUrl(newUrl: String) {
        val urlToSet = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (baseUrl != urlToSet) {
            baseUrl = urlToSet
            _api = null
        }
    }
}
