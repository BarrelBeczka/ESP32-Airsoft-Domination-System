package com.example.esp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.esp.viewmodel.GameViewModel

// Ekran startowy Aplikacji
@Composable
fun SetupScreen(viewModel: GameViewModel, onGameStart: (Int, String) -> Unit) {
    var duration by remember { mutableStateOf("10") }
    var espIp by remember { mutableStateOf("10.0.2.2:8181") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kontroler Dominacji",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = espIp,
            onValueChange = { espIp = it },
            label = { Text("Adres IP ESP32") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Czas gry (w minutach)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.checkConnection(espIp) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sprawdź łączność")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = viewModel.connectionMessage, color = if (viewModel.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val durMin = duration.toIntOrNull() ?: 10
                val durSec = durMin * 60
                viewModel.setEspIp(espIp)
                viewModel.startGame(durSec) 
                onGameStart(durSec, espIp)
            },
            enabled = viewModel.isConnected,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Rozpocznij grę")
        }
    }
}
