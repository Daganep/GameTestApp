package com.neway.gametestapp.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.neway.gametestapp.game.model.Point

/**
 * Draws the Snake game board: grid, food, and snake.
 *
 * @param snake    list of cell coordinates, head first.
 * @param food     current food cell.
 * @param gridSize number of cells per side (square grid).
 * @param modifier applied to the outer [Box].
 */
@Composable
fun GameBoard(
    snake: List<Point>,
    food: Point,
    gridSize: Int,
    modifier: Modifier = Modifier
) {
    val boardColor = MaterialTheme.colorScheme.surfaceContainer
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val snakeColor = MaterialTheme.colorScheme.primary
    val foodColor = Color(0xFFE53935)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / gridSize

            // Background
            drawRect(color = boardColor)

            // Grid lines
            for (i in 1 until gridSize) {
                val pos = i * cellSize
                drawLine(gridColor, Offset(pos, 0f), Offset(pos, size.height), strokeWidth = 1f)
                drawLine(gridColor, Offset(0f, pos), Offset(size.width, pos), strokeWidth = 1f)
            }

            // Food (filled circle centred in its cell)
            val foodCx = (food.x + 0.5f) * cellSize
            val foodCy = (food.y + 0.5f) * cellSize
            drawCircle(
                color = foodColor,
                radius = cellSize * 0.35f,
                center = Offset(foodCx, foodCy)
            )

            // Snake segments (rounded rects; head full opacity, body 75 %)
            snake.forEachIndexed { index, point ->
                val isHead = index == 0
                val pad = cellSize * 0.1f
                val left = point.x * cellSize + pad
                val top = point.y * cellSize + pad
                val w = cellSize - pad * 2
                drawRoundRect(
                    color = if (isHead) snakeColor else snakeColor.copy(alpha = 0.75f),
                    topLeft = Offset(left, top),
                    size = Size(w, w),
                    cornerRadius = CornerRadius(cellSize * 0.3f)
                )
            }
        }
    }
}
