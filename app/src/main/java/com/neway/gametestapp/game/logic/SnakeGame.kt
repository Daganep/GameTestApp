package com.neway.gametestapp.game.logic

import com.neway.gametestapp.game.model.Direction
import com.neway.gametestapp.game.model.GameConfig
import com.neway.gametestapp.game.model.GameState
import com.neway.gametestapp.game.model.Point
import kotlin.random.Random

/**
 * Pure, UI-free snake game state machine (Stage 1.2).
 *
 * The grid is [GameConfig.size] × [GameConfig.size]. The snake is stored head-first.
 *
 * A [step] advances the head by one cell in [direction]:
 *  - wall or self collision → [GameState.GAME_OVER]
 *  - landing on [food] → grow, spawn a new food, +10 score
 *  - otherwise → drop the tail (length unchanged)
 *
 * [random] is injectable so tests can be deterministic. Not thread-safe:
 * intended to be driven from a single game-loop thread.
 */
class SnakeGame(
    val config: GameConfig = GameConfig(),
    private val random: Random = Random.Default
) {

    var snake: List<Point> = emptyList(); internal set
    var direction: Direction = Direction.RIGHT; internal set
    var pendingDirection: Direction? = null; internal set
    var food: Point = Point(-1, -1); internal set
    var score: Int = 0; internal set
    var state: GameState = GameState.PLAYING; private set

    /**
     * Current tick interval in ms, derived from [score]: every
     * [GameConfig.speedUpEvery] food units the interval drops by
     * [GameConfig.intervalStepMs], never below [GameConfig.minIntervalMs].
     */
    val tickIntervalMs: Long
        get() {
            val foodsEaten = score / 10
            val speedUps = foodsEaten / config.speedUpEvery
            return (config.startIntervalMs - speedUps.toLong() * config.intervalStepMs)
                .coerceAtLeast(config.minIntervalMs)
        }

    init {
        reset()
    }

    /**
     * Restores the initial layout: a 3-segment snake centred on the grid,
     * moving [Direction.RIGHT], score reset, state [GameState.PLAYING].
     */
    fun reset() {
        val cx = config.size / 2
        val cy = config.size / 2
        snake = listOf(
            Point(cx, cy),
            Point(cx - 1, cy),
            Point(cx - 2, cy)
        )
        direction = Direction.RIGHT
        pendingDirection = null
        score = 0
        state = GameState.PLAYING
        food = FoodSpawner.randomFreeCell(snake, config.size, random) ?: Point(-1, -1)
    }

    /**
     * Request a new [dir]. A 180° reversal (opposite of the effective
     * direction) is rejected. No-op when not [GameState.PLAYING].
     */
    fun changeDirection(dir: Direction) {
        if (state != GameState.PLAYING) return
        val effectiveDir = pendingDirection ?: direction
        if (dir == effectiveDir.opposite()) return
        pendingDirection = dir
    }

    fun pause() {
        if (state == GameState.PLAYING) state = GameState.PAUSED
    }

    fun resume() {
        if (state == GameState.PAUSED) state = GameState.PLAYING
    }

    /**
     * Advances the simulation by one cell. No-op when not [GameState.PLAYING].
     */
    fun step() {
        if (state != GameState.PLAYING) return

        // Apply pending direction at the start of the tick.
        val pending = pendingDirection
        if (pending != null) {
            direction = pending
            pendingDirection = null
        }

        val head = snake.first()
        val newHead = Point(head.x + direction.dx, head.y + direction.dy)

        if (CollisionDetector.hitWall(newHead, config.size)) {
            state = GameState.GAME_OVER
            return
        }

        val ate = newHead == food
        // On a normal move the tail vacates, so it is not a valid self-collision target.
        val bodyToCheck = if (ate) snake else snake.dropLast(1)
        if (CollisionDetector.hitSelf(newHead, bodyToCheck)) {
            state = GameState.GAME_OVER
            return
        }

        if (ate) {
            snake = listOf(newHead) + snake
            score += 10
            food = FoodSpawner.randomFreeCell(snake, config.size, random) ?: Point(-1, -1)
        } else {
            snake = listOf(newHead) + snake.dropLast(1)
        }
    }
}

