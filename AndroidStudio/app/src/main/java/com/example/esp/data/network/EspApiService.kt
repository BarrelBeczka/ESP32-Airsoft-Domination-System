package com.example.esp.data.network

import com.example.esp.data.model.GameStatus
import com.example.esp.data.model.MatchResult
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
//interfejs retrofit definiujący komunikację z serwerem HTTP na ESP32
interface EspApiService {
    // Sprawdzanie łączności
    @GET("status")
    suspend fun getEspStatus(): String
    // Pobieranie stanu gry
    @GET("game/status")
    suspend fun getGameStatus(): GameStatus
    // Rozpoczęcie nowej gry
    @POST("game/start")
    suspend fun startGame(@Query("duration") seconds: Int): GameStatus
    // Ręczne zatrzymanie gry
    @POST("game/stop")
    suspend fun stopGame(): GameStatus
    // Przejęcie punktu z aplikacji
    @POST("game/press")
    suspend fun pressButton(@Query("team") team: String): GameStatus
    // Pobranie historii
    @GET("history")
    suspend fun getHistory(): List<MatchResult>
}
