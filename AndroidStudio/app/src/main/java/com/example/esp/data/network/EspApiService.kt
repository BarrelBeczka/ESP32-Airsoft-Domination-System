package com.example.esp.data.network

import com.example.esp.data.model.GameStatus
import com.example.esp.data.model.MatchResult
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EspApiService {
    @GET("status")
    suspend fun getEspStatus(): String

    @GET("game/status")
    suspend fun getGameStatus(): GameStatus

    @POST("game/start")
    suspend fun startGame(@Query("duration") seconds: Int): GameStatus

    @POST("game/stop")
    suspend fun stopGame(): GameStatus

    @POST("game/press")
    suspend fun pressButton(@Query("team") team: String): GameStatus

    @GET("history")
    suspend fun getHistory(): List<MatchResult>
}
