package com.example.esp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp.data.model.GameStatus
import com.example.esp.data.model.MatchResult
import com.example.esp.data.network.EspRetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Mózg aplikacji przechowuje i zarządza całym UI
class GameViewModel : ViewModel() {

    var gameStatus by mutableStateOf(GameStatus())
        private set

    var matchHistory by mutableStateOf(emptyList<MatchResult>())
        private set

    var isConnected by mutableStateOf(false)
        private set

    var connectionMessage by mutableStateOf("")
        private set

    // Aktualizacja adresu IP ESP w kliencie
    fun setEspIp(ip: String) {
        val url = if (ip.startsWith("http://")) ip else "http://$ip"
        EspRetrofitClient.updateBaseUrl(url)
    }

    // Dzieki launch mamy asynchroniczne zapytania
    fun checkConnection(ip: String) {
        setEspIp(ip)
        viewModelScope.launch {
            try {
                connectionMessage = "Sprawdzanie łączności..."
                val response = EspRetrofitClient.getDynamicApi().getEspStatus()
                isConnected = true
                connectionMessage = "Połączono pomyślnie z ESP32!"
            } catch (e: Exception) {
                isConnected = false
                connectionMessage = "Błąd łączności: ${e.message}"
            }
        }
    }

    fun startGame(duration: Int) {
        // Zabezpieczenie przed przedwczesnym powrotem do ekranu wyników
        gameStatus = gameStatus.copy(finished = false, gameActive = true)
        viewModelScope.launch {
            try {
                gameStatus = EspRetrofitClient.getDynamicApi().startGame(duration)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopGame() {
        viewModelScope.launch {
            try {
                gameStatus = EspRetrofitClient.getDynamicApi().stopGame()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun pressButton(team: String) {
        viewModelScope.launch {
            try {
                gameStatus = EspRetrofitClient.getDynamicApi().pressButton(team)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Pętla odpytująca ESP32 o stan gry
    fun pollStatus() {
        viewModelScope.launch {
            while (true) {
                try {
                    gameStatus = EspRetrofitClient.getDynamicApi().getGameStatus()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(1000)
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                matchHistory = EspRetrofitClient.getDynamicApi().getHistory()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
