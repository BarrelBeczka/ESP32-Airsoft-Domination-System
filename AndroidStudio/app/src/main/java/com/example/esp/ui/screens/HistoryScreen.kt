package com.example.esp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.esp.data.model.MatchResult
import com.example.esp.viewmodel.GameViewModel

// Ekran Histoii
@Composable
fun HistoryScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    
    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Historia spotkań", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (viewModel.matchHistory.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Brak historii", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.matchHistory) { match ->
                    MatchCard(match)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Powrót")
        }
    }
}

@Composable
fun MatchCard(match: MatchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mecz #${match.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Czas gry: ${match.duration}s", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Niebiescy: ${match.blueTime}s", color = Color(0xFF2196F3))
                Text("Czerwoni: ${match.redTime}s", color = Color(0xFFF44336))
            }
            Spacer(modifier = Modifier.height(8.dp))
            val winnerText = when (match.winner) {
                "blue" -> "Wygrana: NIEBIESCY"
                "red" -> "Wygrana: CZERWONI"
                else -> "REMIS"
            }
            Text(winnerText, style = MaterialTheme.typography.labelLarge)
        }
    }
}
