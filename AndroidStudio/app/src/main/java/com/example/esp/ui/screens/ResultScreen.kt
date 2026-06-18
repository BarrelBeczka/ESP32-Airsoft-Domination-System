package com.example.esp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.esp.viewmodel.GameViewModel

// Ekran wyników
@Composable
fun ResultScreen(
    viewModel: GameViewModel,
    onNewGame: () -> Unit,
    onShowHistory: () -> Unit
) {
    val status = viewModel.gameStatus
    
    val winnerColor = when (status.winner) {
        "blue" -> Color(0xFF2196F3)
        "red" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    
    val winnerText = when (status.winner) {
        "blue" -> "WYGRYWA NIEBIESKI"
        "red" -> "WYGRYWA CZERWONY"
        else -> "REMIS"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Zwycięzca:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = winnerText,
            style = MaterialTheme.typography.displayMedium,
            color = winnerColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text("Statystyki:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Czas Niebieskich: ${status.blueTime}s", color = Color(0xFF2196F3), style = MaterialTheme.typography.titleMedium)
        Text("Czas Czerwonych: ${status.redTime}s", color = Color(0xFFF44336), style = MaterialTheme.typography.titleMedium)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth()) {
            Text("Nowa gra")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onShowHistory, modifier = Modifier.fillMaxWidth()) {
            Text("Zobacz historię")
        }
    }
}
