package com.example.esp.navigation

import kotlinx.serialization.Serializable

@Serializable
object SetupRoute

@Serializable
data class GameRoute(val duration: Int, val espIp: String)

@Serializable
data class ResultRoute(val blueTime: Int, val redTime: Int, val winner: String)

@Serializable
object HistoryRoute
