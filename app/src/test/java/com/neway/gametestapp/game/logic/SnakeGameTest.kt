package com.neway.gametestapp.game.logic

import com.neway.gametestapp.game.model.Direction
import com.neway.gametestapp.game.model.GameConfig
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
    fun `reset clears score and resets to initial state`() {
        val g = game()
        g.direction = Direction.DOWN
        g.reset()
        assertEquals(0, g.score)
        assertFalse(g.isGameOver)
        assertEquals(Direction.RIGHT, g.direction)
        assertEquals(3, g.snake.size)
    }

    @Test
    fun `reset food is on a free cell within bounds`() {
        val g = game()
        val size = g.config.size
        assertTrue(g.food.x in 0 until size)
        assertTrue(g.food.y in 0 until g.config.rows)
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
        assertNull(g.pendingDirection)
        g.direction = Direction.UP
        g.changeDirection(Direction.DOWN)
        assertNull(g.pendingDirection)
    }

    @Test
    fun `changeDirection sets pendingDirection for perpendicular turns`() {
        val g = game()
        g.direction = Direction.RIGHT
        g.changeDirection(Direction.UP)
        assertEquals(Direction.UP, g.pendingDirection)
    }

    @Test
    fun `changeDirection is ignored after game over`() {
        val g = game()
        g.snake = listOf(Point(0, 5), Point(1, 5), Point(2, 5))
        g.direction = Direction.LEFT
        g.food = Point(-1, -1)
        g.step()
        assertTrue(g.isGameOver)
        g.changeDirection(Direction.UP)
        assertNull(g.pendingDirection)
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
        assertEquals(scoreBefore + 10, g.score)
        assertTrue(g.food != Point(6, 5))
    }

    @Test
    fun `eating food decreases speedMs and floors at minSpeedMs`() {
        val g = game(config = GameConfig(speedDecrementMs = 5L, minSpeedMs = 90L, startSpeedMs = 100L))
        val speedBefore = g.speedMs
        g.snake = listOf(Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(6, 5)
        g.step()
        assertEquals(speedBefore - 5L, g.speedMs)

        g.snake = listOf(Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(6, 5)
        g.speedMs = 95L
        g.step()
        assertEquals(90L, g.speedMs)
    }

    @Test
    fun `hitting the left wall ends the game`() {
        val g = game()
        g.snake = listOf(Point(0, 5), Point(1, 5), Point(2, 5))
        g.direction = Direction.LEFT
        g.food = Point(-1, -1)
        g.step()
        assertTrue(g.isGameOver)
    }

    @Test
    fun `hitting the right wall ends the game`() {
        val g = game()
        val size = g.config.size
        g.snake = listOf(Point(size - 1, 5), Point(size - 2, 5), Point(size - 3, 5))
        g.direction = Direction.RIGHT
        g.food = Point(-1, -1)
        g.step()
        assertTrue(g.isGameOver)
    }

    @Test
    fun `hitting the top wall ends the game`() {
        val g = game()
        g.snake = listOf(Point(5, 0), Point(5, 1), Point(5, 2))
        g.direction = Direction.UP
        g.food = Point(-1, -1)
        g.step()
        assertTrue(g.isGameOver)
    }

    @Test
    fun `hitting the bottom wall ends the game`() {
        val g = game()
        val size = g.config.size
        g.snake = listOf(Point(5, g.config.rows - 1), Point(5, g.config.rows - 2), Point(5, g.config.rows - 3))
        g.direction = Direction.DOWN
        g.food = Point(-1, -1)
        g.step()
        assertTrue(g.isGameOver)
    }

    @Test
    fun `moving into the own body ends the game`() {
        val g = game()
        g.snake = listOf(Point(4, 4), Point(5, 4), Point(5, 5), Point(4, 5), Point(3, 5))
        g.direction = Direction.DOWN
        g.food = Point(-1, -1)
        g.step()
        assertTrue(g.isGameOver)
    }

    @Test
    fun `step is a no-op after game over`() {
        val g = game()
        g.snake = listOf(Point(0, 5), Point(1, 5), Point(2, 5))
        g.direction = Direction.LEFT
        g.food = Point(-1, -1)
        g.step()
        assertTrue(g.isGameOver)
        val afterGameOver = g.snake.toList()
        g.step()
        assertEquals(afterGameOver, g.snake)
    }

    @Test
    fun `game works on a small grid`() {
        val g = game(config = GameConfig(size = 5, rows = 5))
        assertEquals(5, g.config.size)
        assertEquals(2, g.snake.first().x)
        assertEquals(2, g.snake.first().y)
        assertTrue(g.food.x in 0 until 5)
        assertTrue(g.food.y in 0 until 5)
    }

    // -- FoodSpawner direct coverage --

    @Test
    fun `randomFreeCell returns null when the box is full`() {
        val size = 3
        val full = (0 until size).flatMap { x -> (0 until size).map { y -> Point(x, y) }}
        assertNull(FoodSpawner.randomFreeCell(full, size, size, Random(1L)))
    }

    @Test
    fun `randomFreeCell always lands on a free cell within bounds`() {
        val size = 8
        val snake = listOf(Point(0, 0), Point(1, 0), Point(2, 0))
        repeat(500) { i ->
            val cell = FoodSpawner.randomFreeCell(snake, size, size, Random(i.toLong()))
            assertNotNull("food was null for seed $i", cell)
            assertTrue("x out of bounds: $cell", cell!!.x in 0 until size)
            assertTrue("y out of bounds: $cell", cell.y in 0 until size)
            assertFalse("food spawned on snake: $cell", cell in snake)
        }
    }
}
