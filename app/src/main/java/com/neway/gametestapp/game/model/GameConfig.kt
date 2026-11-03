package com.neway.gametestapp.game.model

/**
 * Tunable parameters for a [SnakeGame] instance.
 *
 * @param size grid width in cells (columns).
 * @param rows grid height in cells (rows).
 * @param startSpeedMs initial tick interval in milliseconds (150 ms).
 * @param minSpeedMs lowest allowed tick interval (90 ms).
 * @param speedDecrementMs how much faster each tick gets per food eaten (5 ms).
 */
data class GameConfig(
    val size: Int = 15,
    val rows: Int = 26,
    val startSpeedMs: Long = 150L,
    val minSpeedMs: Long = 90L,
    val speedDecrementMs: Long = 5L
)
