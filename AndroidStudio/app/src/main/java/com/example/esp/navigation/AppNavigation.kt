package com.example.esp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.esp.ui.screens.GameScreen
import com.example.esp.ui.screens.HistoryScreen
import com.example.esp.ui.screens.ResultScreen
import com.example.esp.ui.screens.SetupScreen
import com.example.esp.viewmodel.GameViewModel

@Composable
fun AppNavigation(viewModel: GameViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SetupRoute) {
        
        composable<SetupRoute> {
            SetupScreen(
                viewModel = viewModel,
                onGameStart = { duration, espIp ->
                    navController.navigate(GameRoute(duration, espIp)) {
                        popUpTo(SetupRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<GameRoute> {
            GameScreen(
                viewModel = viewModel,
                onGameEnd = { blueTime, redTime, winner ->
                    navController.navigate(ResultRoute(blueTime, redTime, winner)) {
                        popUpTo(SetupRoute) { inclusive = false }
                    }
                }
            )
        }

        composable<ResultRoute> {
            ResultScreen(
                viewModel = viewModel,
                onNewGame = {
                    navController.navigate(SetupRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onShowHistory = {
                    navController.navigate(HistoryRoute)
                }
            )
        }

        composable<HistoryRoute> {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
