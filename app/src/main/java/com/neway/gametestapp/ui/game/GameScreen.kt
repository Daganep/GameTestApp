package com.neway.gametestapp.ui.game

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.neway.gametestapp.R
import com.neway.gametestapp.ui.gameover.GameOverScreen
import com.neway.gametestapp.util.swipeDetector

/**
 * Main game screen: score bar and a full-height game board (swipe-enabled).
 * Shows [GameOverScreen] as a full-screen overlay when the game ends.
 *
 * @param onExit  called when the user dismisses the game (top-bar ✕ or Game-Over screen).
 * @param viewModel  obtained from [ViewModelProvider] (with an explicit factory, since
 *                   [GameViewModel] has no single-`Application` constructor) by default;
 *                   pass explicitly to share an existing instance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onExit: () -> Unit,
    viewModel: GameViewModel = defaultGameViewModel()
) {
    var state by remember { mutableStateOf(viewModel.uiState.value) }

    // Start (or restart) the game the first time the game screen appears.
    // Without this the timer never starts, so the snake stays still after
    // pressing "Play" in the menu.
    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    LaunchedEffect(Unit) {
        viewModel.uiState.collect { state = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onExit) {
                        Text(stringResource(R.string.close_button), fontSize = 22.sp)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Score bar ──────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.score_label, state.score),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.speed_label, state.speedMs),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // ── Game board (swipe to steer) ─────────────────────────────
                // Sized by the grid's aspect ratio, so all cells stay square
                // (the board grows by adding rows, not by stretching cells).
                GameBoard(
                    snake = state.snake,
                    food = state.food,
                    gridWidth = state.size,
                    gridHeight = state.rows,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .aspectRatio(state.size.toFloat() / state.rows)
                        .swipeDetector(onDirection = { dir -> viewModel.changeDirection(dir) })
                )
            }

            // ── Game-over overlay ──────────────────────────────────────────
            if (state.isGameOver) {
                GameOverScreen(
                    score = state.score,
                    isNewRecord = state.score > 0 && state.score == state.bestScore,
                    onRestart = { viewModel.startGame() },
                    onExit = onExit
                )
            }
        }
    }
}

/**
 * Creates a [GameViewModel] via an explicit [ViewModelProvider.Factory], since
 * [GameViewModel] has no single-`Application` constructor and the default
 * [androidx.lifecycle.AndroidViewModelFactory] cannot instantiate it.
 */
@Composable
private fun defaultGameViewModel(): GameViewModel {
    val context = LocalContext.current
    return ViewModelProvider(
        context as ViewModelStoreOwner,
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(context.applicationContext as Application) as T
            }
        }
    )[GameViewModel::class.java]
}
