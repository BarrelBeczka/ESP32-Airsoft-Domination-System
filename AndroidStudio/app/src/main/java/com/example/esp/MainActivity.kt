package com.example.esp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.esp.navigation.AppNavigation
import com.example.esp.ui.theme.ESPTheme

// Jedyna aktywność aplikacji nawigacja dzięki @composable z jetpack compose navi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Rysowanie pod paskami systemowymi rozciągnięcie na cały ekran
        enableEdgeToEdge()

        // Ustawienie zawartości ekranu - Jetpack Compose
        setContent {
            ESPTheme {
                // Szkielet ekranu sprawdzający marginesy
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Nawigacja między ekranami
                        AppNavigation()
                    }
                }
            }
        }
    }
}