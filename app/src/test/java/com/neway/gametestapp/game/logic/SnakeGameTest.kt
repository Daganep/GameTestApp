package com.neway.gametestapp.game.logic

import com.neway.gametestapp.game.model.Direction
import com.neway.gametestapp.game.model.GameConfig
import com.neway.gametestapp.game.model.GameState
import com.neway.gametestapp.game.model.Point
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeGameTest {

    private fun game(config: GameConfig = GameConfig(), random: Random = Random(1234)) =
        SnakeGame(config, random)

    @Test
    fun `reset places a 3 segment snake at centre moving right`() {
        val g = game()
        val cx = GameConfig().size / 2
        val cy = GameConfig().size / 2
        assertEquals(3, g.snake.size)
        assertEquals(Point(cx, cy), g.snake.first())
        assertEquals(Point(cx - 1, cy), g.snake[1])
        assertEquals(Point(cx - 2, cy), g.snake[2])
        assertEquals(Direction.RIGHT, g.direction)
    }

    @Test
    fun `reset clears score and sets state to PLAYING`() {
        val g = game()
        g.score = 42
        g.pause()
        assertEquals(GameState.PAUSED, g.state)
        g.reset()
        assertEquals(0, g.score)
        assertEquals(GameState.PLAYING, g.state)
        assertEquals(Direction.RIGHT, g.direction)
    }

    @Test
    fun `reset food is on a free cell within bounds`() {
        val g = game()
        val size = g.config.size
        assertTrue(g.food.x in 0 until size)
        assertTrue(g.food.y in 0 until size)
        assertTrue(g.food !in g.snake)
    }

    @Test
    fun `step moves head to the right`() {
        val g = game()
        g.snake = listOf(Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(-1, -1)
        g.step()
        assertEquals(Point(6, 5), g.snake.first())
    }

    @Test
    fun `step moves head to the left`() {
        val g = game()
        g.snake = listOf(Point(5, 5), Point(6, 5), Point(7, 5))
        g.direction = Direction.LEFT
        g.food = Point(-1, -1)
        g.step()
        assertEquals(Point(4, 5), g.snake.first())
    }

    @Test
    fun `step moves head up`() {
        val g = game()
        g.snake = listOf(Point(5, 5), Point(5, 6), Point(5, 7))
        g.direction = Direction.UP
        g.food = Point(-1, -1)
        g.step()
        assertEquals(Point(5, 4), g.snake.first())
    }

    @Test
    fun `step moves head down`() {
        val g = game()
        g.snake = listOf(Point(5, 5), Point(5, 4), Point(5, 3))
        g.direction = Direction.DOWN
        g.food = Point(-1, -1)
        g.step()
        assertEquals(Point(5, 6), g.snake.first())
    }

    @Test
    fun `step drops the tail when not eating`() {
        val g = game()
        g.snake = listOf(Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(-1, -1)
        val before = g.snake.size
        g.step()
        assertEquals(before, g.snake.size)
        assertEquals(listOf(Point(6, 5), Point(5, 5), Point(4, 5)), g.snake)
    }

    @Test
    fun `changeDirection rejects a 180 degree reversal`() {
        val g = game()
        g.direction = Direction.RIGHT
        g.changeDirection(Direction.LEFT)
        assertEquals(Direction.RIGHT, g.direction)
        g.direction = Direction.UP
        g.changeDirection(Direction.DOWN)
        assertEquals(Direction.UP, g.direction)
    }

    @Test
    fun `changeDirection accepts perpendicular turns`() {
        val g = game()
        g.direction = Direction.RIGHT
        g.changeDirection(Direction.UP)
        assertEquals(Direction.UP, g.direction)
        g.changeDirection(Direction.LEFT)
        assertEquals(Direction.LEFT, g.direction)
    }

    @Test
    fun `changeDirection is ignored when not playing`() {
        val g = game()
        g.pause()
        g.changeDirection(Direction.UP)
        assertEquals(Direction.RIGHT, g.direction)
    }

    @Test
    fun `eating food grows the snake and increments score`() {
        val g = game()
        g.snake = listOf(Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(6, 5)
        val scoreBefore = g.score
        g.step()
        assertEquals(4, g.snake.size)
        assertEquals(Point(6, 5), g.snake.first())
        assertEquals(scoreBefore + 1, g.score)
        assertEquals(GameState.PLAYING, g.state)
    }

    @Test
    fun `eating food spawns a new food on a free cell`() {
        val g = game(random = Random(7))
        g.snake = listOf(Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(6, 5)
        g.step()
        val size = g.config.size
        assertTrue(g.food.x in 0 until size)
        assertTrue(g.food.y in 0 until size)
        assertTrue(g.food !in g.snake)
    }

    @Test
    fun `currentIntervalMs follows the speed-up threshold`() {
        val g = game()
        assertEquals(150L, g.currentIntervalMs)
        g.score = 4
        assertEquals(150L, g.currentIntervalMs)
        g.score = 5
        assertEquals(140L, g.currentIntervalMs)
        g.score = 10
        assertEquals(130L, g.currentIntervalMs)
        g.score = 45
        assertEquals(60L, g.currentIntervalMs)
        g.score = 999
        assertEquals(60L, g.currentIntervalMs)
    }

    @Test
    fun `hitting the left wall ends the game`() {
        val g = game()
        g.snake = listOf(Point(0, 5), Point(1, 5), Point(2, 5))
        g.direction = Direction.LEFT
        g.food = Point(-1, -1)
        g.step()
        assertEquals(GameState.GAME_OVER, g.state)
    }

    @Test
    fun `hitting the right wall ends the game`() {
        val g = game()
        val size = g.config.size
        g.snake = listOf(Point(size - 1, 5), Point(size - 2, 5), Point(size - 3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(-1, -1)
        g.step()
        assertEquals(GameState.GAME_OVER, g.state)
    }

    @Test
    fun `hitting the top wall ends the game`() {
        val g = game()
        g.snake = listOf(Point(5, 0), Point(5, 1), Point(5, 2))
        g.direction = Direction.UP
        g.food = Point(-1, -1)
        g.step()
        assertEquals(GameState.GAME_OVER, g.state)
    }

    @Test
    fun `hitting the bottom wall ends the game`() {
        val g = game()
        val size = g.config.size
        g.snake = listOf(Point(5, size - 1), Point(5, size - 2), Point(5, size - 3))
        g.direction = Direction.DOWN
        g.food = Point(-1, -1)
        g.step()
        assertEquals(GameState.GAME_OVER, g.state)
    }

    @Test
    fun `moving into the own body ends the game`() {
        val g = game()
        g.snake = listOf(Point(4, 4), Point(5, 4), Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.DOWN
        g.food = Point(-1, -1)
        g.step()
        assertEquals(GameState.GAME_OVER, g.state)
    }

    @Test
    fun `pause sets state to PAUSED`() {
        val g = game()
        g.pause()
        assertEquals(GameState.PAUSED, g.state)
    }

    @Test
    fun `resume returns state to PLAYING`() {
        val g = game()
        g.pause()
        g.resume()
        assertEquals(GameState.PLAYING, g.state)
    }

    @Test
    fun `step is a no-op when the game is paused`() {
        val g = game()
        g.snake = listOf(Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(-1, -1)
        g.pause()
        g.step()
        assertEquals(Point(5, 5), g.snake.first())
        assertEquals(GameState.PAUSED, g.state)
    }

    @Test
    fun `step is a no-op after game over`() {
        val g = game()
        g.snake = listOf(Point(0, 5), Point(1, 5), Point(2, 5))
        g.direction = Direction.LEFT
        g.food = Point(-1, -1)
        g.step()
        val afterGameOver = g.snake.toList()
        g.step()
        assertEquals(afterGameOver, g.snake)
        assertEquals(GameState.GAME_OVER, g.state)
    }

    @Test
    fun `game works on a small grid`() {
        val g = game(config = GameConfig(size = 5))
        assertEquals(5, g.config.size)
        assertEquals(2, g.snake.first().x)
        assertEquals(2, g.snake.first().y)
        assertTrue(g.food.x in 0 until 5)
        assertTrue(g.food.y in 0 until 5)
    }

    // ── FoodSpawner direct coverage ─────────────────────────────────────────

    @Test
    fun `randomFreeCell returns null when the board is full`() {
        val size = 3
        val full = (0 until size).flatMap { x -> (0 until size).map { y -> Point(x, y) }}
        assertNull(FoodSpawner.randomFreeCell(full, size, Random(1L)))
    }

    @Test
    fun `randomFreeCell always lands on a free cell within bounds`() {
        val size = 8
        val snake = listOf(Point(0, 0), Point(1, 0), Point(2, 0))
        repeat(500) { i ->
            val cell = FoodSpawner.randomFreeCell(snake, size, Random(i.toLong()))
            assertNotNull("food was null for seed $i", cell)
            assertTrue("x out of bounds: $cell", cell!!.x in 0 until size)
            assertTrue("y out of bounds: $cell", cell.y in 0 until size)
            assertFalse("food spawned on snake: $cell", cell in snake)
        }
    }
}

