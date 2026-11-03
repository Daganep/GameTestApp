package com.neway.gametestapp.ui.game

import android.app.Application
import com.neway.gametestapp.game.logic.SnakeGame
import com.neway.gametestapp.game.model.Direction
import com.neway.gametestapp.game.model.Point
import com.neway.gametestapp.game.storage.ScoreStorage
import com.neway.gametestapp.util.GameTimer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

/**
 * Unit tests for [GameViewModel].
 *
 * Uses mocks for [SnakeGame], [ScoreStorage] and [GameTimer] so the tests
 * run on the plain JVM (no Robolectric needed).
 */
class GameViewModelTest {

    private lateinit var game: SnakeGame
    private lateinit var storage: ScoreStorage
    private lateinit var timer: GameTimer
    private lateinit var application: Application

    @Before
    fun setup() {
        game = mock(SnakeGame::class.java)
        storage = mock(ScoreStorage::class.java)
        timer = mock(GameTimer::class.java)
        application = mock(Application::class.java)
        `when`(game.size).thenReturn(10)
        `when`(game.snake).thenReturn(emptyList())
        `when`(game.food).thenReturn(Point(5, 5))
        `when`(game.score).thenReturn(0)
        `when`(game.isGameOver).thenReturn(false)
        `when`(game.speedMs).thenReturn(150L)
    }

    private fun viewModel(): GameViewModel =
        GameViewModel(application, storage, game) { _ -> timer }

    @Test
    fun `ui state exposes the best score from storage`() {
        `when`(storage.getBest()).thenReturn(42)
        val vm = viewModel()
        assertEquals(42, vm.uiState.value.bestScore)
        assertEquals(150L, vm.uiState.value.speedMs)
    }

    @Test
    fun `step advances the game and updates the ui state`() {
        val vm = viewModel()
        `when`(game.score).thenReturn(7)
        vm.step()
        assertEquals(7, vm.uiState.value.score)
        verify(game).step()
        verify(timer, never()).stop()
    }

    @Test
    fun `step on a finished game stops the timer and saves the best score`() {
        val vm = viewModel()
        `when`(game.isGameOver).thenReturn(false, true)
        `when`(game.score).thenReturn(10)
        vm.step()
        verify(game).step()
        verify(timer).stop()
        verify(storage).updateBest(10)
    }

    @Test
    fun `step after game over does nothing further`() {
        val vm = viewModel()
        `when`(game.isGameOver).thenReturn(true)
        vm.step()
        verify(game, never()).step()
        verify(timer).stop()
        verify(storage, never()).updateBest(0)
    }

    @Test
    fun `change direction delegates to the game`() {
        val vm = viewModel()
        vm.changeDirection(Direction.UP)
        verify(game).changeDirection(Direction.UP)
    }

    @Test
    fun `start game resets a finished game and starts the timer`() {
        val vm = viewModel()
        `when`(game.isGameOver).thenReturn(true)
        vm.startGame()
        verify(game).reset()
        verify(timer).start(150L)
    }
}