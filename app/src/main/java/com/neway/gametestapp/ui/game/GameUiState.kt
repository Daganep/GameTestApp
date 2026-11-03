package com.neway.gametestapp.ui.game

import com.neway.gametestapp.game.logic.SnakeGame
import com.neway.gametestapp.game.model.Point

/**
 * Immutable snapshot of the [SnakeGame] engine state for the UI (Stage 5.1).
 *
 * @param snake snake body; head is the first element.
 * @param food current food cell.
 * @param score current score.
 * @param isGameOver true when the game is over.
 * @param speedMs current tick interval in milliseconds.
 * @param size grid width in cells (columns).
 * @param rows grid height in cells (rows).
 * @param bestScore best score persisted via [com.neway.gametestapp.game.storage.ScoreStorage].
 */
data class GameUiState(
    val snake: List<Point> = emptyList(),
    val food: Point = Point(-1, -1),
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val speedMs: Long = 0L,
    val size: Int = 0,
    val rows: Int = 0,
    val bestScore: Int = 0
) {
    companion object {
        /** Builds a [GameUiState] snapshot from the current [SnakeGame] state. */
        fun from(game: SnakeGame, bestScore: Int = 0): GameUiState = GameUiState(
            snake = game.snake,
            food = game.food,
            score = game.score,
            isGameOver = game.isGameOver,
            speedMs = game.speedMs,
            size = game.size,
            rows = game.rows,
            bestScore = bestScore
        )
    }
}
