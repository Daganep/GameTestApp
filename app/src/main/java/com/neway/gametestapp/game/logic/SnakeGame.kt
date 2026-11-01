package com.neway.gametestapp.game.logic

import com.neway.gametestapp.game.model.Direction
import com.neway.gametestapp.game.model.GameConfig
import com.neway.gametestapp.game.model.Point
import kotlin.random.Random

/**
 * Core Snake game engine.
 *
 * Drives the snake one cell per [step]. Each food eaten grows the snake,
 * adds points, and makes the game slightly faster (per-food speed decrement).
 *
 * @param config game tunables (grid size, speed, …).
 * @param random injectable [Random] for reproducible tests.
 */
class SnakeGame(
    val config: GameConfig = GameConfig(),
    private val random: Random = Random.Default
) {
    /** Grid dimension in cells. */
    val size: Int get() = config.size

    /** Snake body; head is the first element. */
    var snake: List<Point> = emptyList(); internal set

    /** Current movement direction. */
    var direction: Direction = Direction.RIGHT; internal set

    /** Current food cell. */
    var food: Point = Point(-1, -1); internal set

    /** Score, increments by 10 per food eaten. */
    var score: Int = 0; internal set

    /** Current tick interval in milliseconds. */
    var speedMs: Long = config.startSpeedMs; internal set

    /** Direction queued for the next step (if any). */
    var pendingDirection: Direction? = null; internal set

    /** True when the game is over (wall or self collision). */
    var isGameOver: Boolean = false; private set

    init {
        reset()
    }

    /**
     * Advance the game by one tick:
     * 1. apply pending direction, 2. move the snake, 3. handle collisions,
     * 4. check food, grow / respawn.
     */
    fun step() {
        if (isGameOver) return
        pendingDirection?.let { if (it != direction.opposite()) direction = it }
        pendingDirection = null

        val head = snake.first()
        val newHead = Point(head.x + direction.dx, head.y + direction.dy)

        if (CollisionDetector.hitWall(newHead, config.size) ||
            (CollisionDetector.hitSelf(newHead, snake) && snake.last() != newHead)
        ) {
            isGameOver = true
            return
        }

        if (newHead == food) {
            snake = listOf(newHead) + snake
            score += 10
            speedMs = (speedMs - config.speedDecrementMs).coerceAtLeast(config.minSpeedMs)
            val newFood = FoodSpawner.randomFreeCell(snake, config.size, random)
            if (newFood == null) {
                isGameOver = true
                return
            }
            food = newFood
        } else {
            snake = listOf(newHead) + snake.dropLast(1)
        }
    }

    /** Queue [dir] as the next movement direction (rejected if opposite of current). */
    fun changeDirection(dir: Direction) {
        if (isGameOver) return
        if (dir == direction.opposite()) return
        pendingDirection = dir
    }

    /** Reset all game state to the initial values. */
    fun reset() {
        val center = size / 2
        snake = listOf(
            Point(center, center),
            Point(center - 1, center),
            Point(center - 2, center)
        )
        direction = Direction.RIGHT
        score = 0
        speedMs = config.startSpeedMs
        pendingDirection = null
        isGameOver = false
        food = FoodSpawner.randomFreeCell(snake, config.size, random)
            ?: Point(0, 0)
    }
}
