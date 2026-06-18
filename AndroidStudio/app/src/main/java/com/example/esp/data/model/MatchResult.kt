package com.example.esp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
// klasa reprezentująca wynik zakończonego meczu. ESP32 Przechwouje do 10 meczy w tablicy FIFO
// Zwraca tablice JSON do endpointu /history
@Serializable
data class MatchResult(
    @SerialName("id") val id: Int,
    @SerialName("duration") val duration: Int,
    @SerialName("blueTime") val blueTime: Int,
    @SerialName("redTime") val redTime: Int,
    @SerialName("winner") val winner: String
)
