package com.neway.gametestapp.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
 * @param snake      list of cell coordinates, head first.
 * @param food       current food cell.
 * @param gridWidth  number of cells per column.
 * @param gridHeight number of cells per row.
 * @param modifier   applied to the outer [Box]. Give the board dimensions whose
 *                   ratio matches `gridWidth / gridHeight` (e.g.
 *                   `Modifier.fillMaxWidth().aspectRatio(gridWidth / gridHeight)`),
 *                   otherwise cells will be stretched non-squarely.
 */
@Composable
fun GameBoard(
    snake: List<Point>,
    food: Point,
    gridWidth: Int,
    gridHeight: Int,
    modifier: Modifier = Modifier
) {
    val boardColor = MaterialTheme.colorScheme.surfaceContainer
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val snakeColor = MaterialTheme.colorScheme.primary
    val foodColor = Color(0xFFE53935)

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / gridWidth
            val cellH = size.height / gridHeight

            // Background
            drawRect(color = boardColor)

            // Grid lines
            for (i in 1 until gridWidth) {
                val pos = i * cellW
                drawLine(gridColor, Offset(pos, 0f), Offset(pos, size.height), strokeWidth = 1f)
            }
            for (j in 1 until gridHeight) {
                val pos = j * cellH
                drawLine(gridColor, Offset(0f, pos), Offset(size.width, pos), strokeWidth = 1f)
            }

            // Food (filled circle centred in its cell)
            val foodCx = (food.x + 0.5f) * cellW
            val foodCy = (food.y + 0.5f) * cellH
            drawCircle(
                color = foodColor,
                radius = cellW * 0.35f,
                center = Offset(foodCx, foodCy)
            )

            // Snake segments (rounded rects; head full opacity, body 75 %)
            snake.forEachIndexed { index, point ->
                val isHead = index == 0
                val padX = cellW * 0.1f
                val padY = cellH * 0.1f
                val left = point.x * cellW + padX
                val top = point.y * cellH + padY
                val w = cellW - padX * 2
                val h = cellH - padY * 2
                drawRoundRect(
                    color = if (isHead) snakeColor else snakeColor.copy(alpha = 0.75f),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(cellW * 0.3f)
                )
            }
        }
    }
}
