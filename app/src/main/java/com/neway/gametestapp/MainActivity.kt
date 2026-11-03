package com.neway.gametestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.neway.gametestapp.game.storage.PrefsScoreStorage
import com.neway.gametestapp.ui.game.GameScreen
import com.neway.gametestapp.ui.gameover.GameOverScreen
import com.neway.gametestapp.ui.howto.HowToScreen
import com.neway.gametestapp.ui.menu.MainMenuScreen
import com.neway.gametestapp.ui.theme.GameTestAppTheme

/**
 * Single screen state for the whole app (Stage 3.2).
 * Navigation is state-based — no Jetpack Navigation (only 4 screens).
 */
sealed class Screen {
    object Menu : Screen()
    object Game : Screen()
    object HowTo : Screen()
    data class GameOver(val score: Int, val isNewRecord: Boolean) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameTestAppTheme {
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    var screen by remember { mutableStateOf<Screen>(Screen.Menu) }
    val scoreStorage = remember { PrefsScoreStorage(context) }

    val currentScreen = screen
    when (currentScreen) {
        Screen.Menu -> MainMenuScreen(
            bestScore = scoreStorage.getBest(),
            onPlay = { screen = Screen.Game },
            onHowTo = { screen = Screen.HowTo }
        )

        Screen.Game -> GameScreen(onExit = { screen = Screen.Menu })

        Screen.HowTo -> HowToScreen(
            onBack = { screen = Screen.Menu }
        )

        is Screen.GameOver -> GameOverScreen(
            score = currentScreen.score,
            isNewRecord = currentScreen.isNewRecord,
            onRestart = { screen = Screen.Game },
            onExit = { screen = Screen.Menu }
        )
    }
}