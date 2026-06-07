package com.example.esp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameStatus(
    @SerialName("gameActive") val gameActive: Boolean = false,
    @SerialName("finished") val finished: Boolean = false,
    @SerialName("timeLeft") val timeLeft: Int = 0,
    @SerialName("totalDuration") val totalDuration: Int = 0,
    @SerialName("blueTime") val blueTime: Int = 0,
    @SerialName("redTime") val redTime: Int = 0,
    @SerialName("currentOwner") val currentOwner: String = "none",
    @SerialName("winner") val winner: String = ""
)
