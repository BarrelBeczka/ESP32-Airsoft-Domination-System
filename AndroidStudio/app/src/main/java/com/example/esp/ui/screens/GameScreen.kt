package com.example.esp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp.viewmodel.GameViewModel

// Ekran Gry
@Composable
fun GameScreen(viewModel: GameViewModel, onGameEnd: (Int, Int, String) -> Unit) {
    val status = viewModel.gameStatus

    LaunchedEffect(Unit) {
        viewModel.pollStatus()
    }

    // Automatyczne przejście na ekran wyniku gdy gra się skończy
    LaunchedEffect(status.finished) {
        if (status.finished) {
            onGameEnd(status.blueTime, status.redTime, status.winner)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Wielki Timer w sekundach
        Text(
            text = "${status.timeLeft}s",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Text("Aktualny właściciel:", style = MaterialTheme.typography.titleLarge)
        val ownerText = when (status.currentOwner) {
            "blue" -> "NIEBIESKI"
            "red" -> "CZERWONY"
            else -> "NIE PRZEJĘTY"
        }
        val ownerColor = when (status.currentOwner) {
            "blue" -> Color(0xFF2196F3)
            "red" -> Color(0xFFF44336)
            else -> Color.Gray
        }
        Text(text = ownerText, style = MaterialTheme.typography.headlineLarge, color = ownerColor, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.weight(1f))

        // Pasek postępu
        val total = status.blueTime + status.redTime

        if (total == 0) {
            Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.LightGray))
        } else {
            val blueRatio = status.blueTime.toFloat() / total
            Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                if (blueRatio > 0f) Box(modifier = Modifier.weight(blueRatio).fillMaxHeight().background(Color(0xFF2196F3)))
                if (blueRatio < 1f) Box(modifier = Modifier.weight(1f - blueRatio).fillMaxHeight().background(Color(0xFFF44336)))
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${status.blueTime}s", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
            Text("${status.redTime}s", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { viewModel.pressButton("blue") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("Niebieski")
            }
            Button(
                onClick = { viewModel.pressButton("red") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
            ) {
                Text("Czerwony")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.stopGame() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zatrzymaj grę")
        }
    }
}
