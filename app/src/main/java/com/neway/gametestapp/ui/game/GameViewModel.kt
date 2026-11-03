package com.neway.gametestapp.ui.game

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import com.neway.gametestapp.game.logic.SnakeGame
import com.neway.gametestapp.game.model.Direction
import com.neway.gametestapp.game.storage.PrefsScoreStorage
import com.neway.gametestapp.game.storage.ScoreStorage
import com.neway.gametestapp.util.GameTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the game screen.
 *
 * Holds a [SnakeGame] instance and drives it with a [GameTimer].
 * The active game state is exposed as a [GameUiState] flow.
 */
class GameViewModel(
    application: Application,
    private val scoreStorage: ScoreStorage = PrefsScoreStorage(application),
    val game: SnakeGame = SnakeGame(),
    private val createTimer: (onTick: () -> Unit) -> GameTimer = { onTick -> GameTimer(onTick) },
    private val vibrateOnDeath: () -> Unit = { vibrate(application) }
) : AndroidViewModel(application) {

    private val timer: GameTimer by lazy { createTimer({ step() }) }

    /** The best score, loaded from [scoreStorage]. Updated on game over. */
    private var bestScore: Int = scoreStorage.getBest()

    private val _uiState = MutableStateFlow(GameUiState.from(game, bestScore))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /** Starts or restarts the game. */
    fun startGame() {
        if (game.isGameOver) {
            game.reset()
            _uiState.value = GameUiState.from(game, bestScore)
        }
        timer.start(game.speedMs)
    }

    /**
     * Advances the game by one step. Called by the timer on each tick.
     * When the game ends, the timer is stopped and the best score is persisted.
     */
    fun step() {
        if (game.isGameOver) {
            finishGame()
            return
        }
        game.step()
        if (game.isGameOver) {
            finishGame()
        } else {
            _uiState.value = GameUiState.from(game, bestScore)
            timer.updateInterval(game.speedMs)
        }
    }

    private fun finishGame() {
        timer.stop()
        vibrateOnDeath()
        if (game.score > bestScore) {
            bestScore = game.score
            scoreStorage.updateBest(bestScore)
        }
        _uiState.value = GameUiState.from(game, bestScore)
    }

    /** Changes the snake's direction. */
    fun changeDirection(direction: Direction) {
        if (!game.isGameOver) {
            game.changeDirection(direction)
        }
    }

    override fun onCleared() {
        timer.stop()
        super.onCleared()
    }
}

/** Short vibration burst as haptic feedback on death. */
private fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(300L, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(300L)
    }
}