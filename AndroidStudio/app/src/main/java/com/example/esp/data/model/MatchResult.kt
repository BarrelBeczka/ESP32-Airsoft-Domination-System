package com.example.esp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchResult(
    @SerialName("id") val id: Int,
    @SerialName("duration") val duration: Int,
    @SerialName("blueTime") val blueTime: Int,
    @SerialName("redTime") val redTime: Int,
    @SerialName("winner") val winner: String
)
