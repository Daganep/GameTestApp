package com.neway.gametestapp.game.logic

import com.neway.gametestapp.game.model.Point

/**
 * Pure collision checks for the snake.
 */
object CollisionDetector {

    /**
     * `true` when [point] is outside the [size] × [size] grid.
     */
    fun hitWall(point: Point, size: Int): Boolean =
        point.x < 0 || point.y < 0 || point.x >= size || point.y >= size

    /**
     * `true` when [head] overlaps any cell in [body].
     */
    fun hitSelf(head: Point, body: List<Point>): Boolean = body.any { it == head }
}

