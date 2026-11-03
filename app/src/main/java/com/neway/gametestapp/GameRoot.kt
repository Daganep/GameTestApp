package com.neway.gametestapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.neway.gametestapp.game.storage.PrefsScoreStorage
import com.neway.gametestapp.ui.game.GameScreen
import com.neway.gametestapp.ui.howto.HowToScreen
import com.neway.gametestapp.ui.menu.MainMenuScreen
import com.neway.gametestapp.ui.theme.GameTestAppTheme

private enum class Screen { MENU, GAME, HOWTO }

@Composable
fun GameRoot() {
    GameTestAppTheme {
        var screen by remember { mutableStateOf(Screen.MENU) }
        val context = LocalContext.current

        when (screen) {
            Screen.MENU -> {
                val bestScore = PrefsScoreStorage(context).getBest()
                MainMenuScreen(
                    onPlay = { screen = Screen.GAME },
                    onHowTo = { screen = Screen.HOWTO },
                    bestScore = bestScore
                )
            }
            Screen.GAME -> GameScreen(
                onExit = { screen = Screen.MENU }
            )
            Screen.HOWTO -> HowToScreen(
                onBack = { screen = Screen.MENU }
            )
        }
    }
}
