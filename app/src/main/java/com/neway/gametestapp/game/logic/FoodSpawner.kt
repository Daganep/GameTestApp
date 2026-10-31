package com.neway.gametestapp.game.logic

import com.neway.gametestapp.game.model.Point
import kotlin.random.Random

/**
 * Picks a random free cell on the grid.
 * Returns `null` when the board is completely filled (player wins).
 */
object FoodSpawner {

    fun randomFreeCell(
        snake: Collection<Point>,
        size: Int,
        random: Random = Random.Default
    ): Point? {
        val occupied = snake.toSet()
        val free = (0 until size)
            .flatMap { x -> (0 until size).map { y -> Point(x, y) } }
            .filter { it !in occupied }
        return free.randomOrNull(random)
    }
}

