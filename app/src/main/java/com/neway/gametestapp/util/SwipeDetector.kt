package com.neway.gametestapp.util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.neway.gametestapp.game.model.Direction

/**
 * Tracks vertical and horizontal swipe accumulators and determines
 * the dominant [Direction] once [threshold] (in px) is exceeded.
 *
 * Designed to be used inside a `pointerInput { awaitEachGesture { … } }` block,
 * which is the same mechanism behind `detectVerticalSwipeGestures` /
 * `detectHorizontalSwipeGestures`.
 */
class SwipeDetector(private val threshold: Float = 50f) {

    private var verticalAcc = 0f
    private var horizontalAcc = 0f
    private var fired = false

    /** Accumulate a vertical change (positive = down, negative = up). */
    fun onVerticalChange(change: Float) {
        if (!fired) verticalAcc += change
    }

    /** Accumulate a horizontal change (positive = right, negative = left). */
    fun onHorizontalChange(change: Float) {
        if (!fired) horizontalAcc += change
    }

    /**
     * Returns the detected [Direction] if the [threshold] has been exceeded,
     * otherwise `null`. Once a direction is returned, further changes in this
     * gesture are ignored until [reset] is called.
     */
    fun check(): Direction? {
        if (fired) return null
        val vAbs = kotlin.math.abs(verticalAcc)
        val hAbs = kotlin.math.abs(horizontalAcc)
        if (vAbs < threshold && hAbs < threshold) return null
        fired = true
        return when {
            vAbs >= hAbs -> if (verticalAcc < 0) Direction.UP else Direction.DOWN
            else -> if (horizontalAcc < 0) Direction.LEFT else Direction.RIGHT
        }
    }

    /** Reset all accumulated state (call at the start of each new gesture). */
    fun reset() {
        verticalAcc = 0f
        horizontalAcc = 0f
        fired = false
    }
}

/**
 * Attaches a swipe-gesture detector to this [Modifier].
 *
 * When the user swipes past the default 50 px threshold the corresponding
 * [Direction] is delivered to [onDirection].
 *
 * Usage:
 * ```
 * GameBoard(
 *     snake = state.snake,
 *     food = state.food,
 *     gridSize = state.size,
 *     modifier = Modifier
 *         .fillMaxWidth()
 *         .swipeDetector { dir -> viewModel.changeDirection(dir) }
 * )
 * ```
 */
fun Modifier.swipeDetector(
    onDirection: (Direction) -> Unit,
    threshold: Float = 50f
): Modifier = this.pointerInput(Unit) {
    awaitEachGesture {
        val detector = SwipeDetector(threshold)
        var previous = awaitFirstDown(requireUnconsumed = false).position
        while (true) {
            val change = awaitPointerEvent().changes.first()
            detector.onHorizontalChange(change.position.x - previous.x)
            detector.onVerticalChange(change.position.y - previous.y)
            previous = change.position
            change.consume()
            detector.check()?.let { onDirection(it); break }
            if (!change.pressed) break
        }
    }
}
