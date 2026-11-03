package com.neway.gametestapp.util

import android.os.Handler
import android.os.Looper

/**
 * Handler-based game tick ticker (Stage 5.2).
 *
 * Uses [Handler.postDelayed] on the main looper with a dynamic interval.
 * Supports start / pause / stop and guaranteed [cancel] (removes the
 * pending message).
 *
 * @param onTick callback invoked on every tick.
 */
class GameTimer(private val onTick: () -> Unit) {

    private val handler = Handler(Looper.getMainLooper())
    private var intervalMs: Long = 0L
    private var running: Boolean = false

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            onTick()
            if (running) {
                handler.postDelayed(this, intervalMs)
            }
        }
    }

    /**
     * Starts (or resumes) the tick loop with the given initial [intervalMs].
     * If already running, only the interval is updated.
     */
    fun start(intervalMs: Long) {
        this.intervalMs = intervalMs
        if (running) {
            // Already running — interval change takes effect on next tick.
            return
        }
        running = true
        handler.postDelayed(tickRunnable, intervalMs)
    }

    /** Updates the interval for the *next* tick (dynamic speed change). */
    fun updateInterval(ms: Long) {
        intervalMs = ms
    }

    /** Pauses the ticker; can be resumed with [start]. */
    fun pause() {
        running = false
        handler.removeCallbacks(tickRunnable)
    }

    /** Stops the ticker permanently (alias for [pause]). */
    fun stop() = pause()

    /**
     * Guarantees removal of the pending delayed message.
     * Called from [androidx.lifecycle.ViewModel.onCleared].
     */
    fun cancel() {
        running = false
        handler.removeCallbacks(tickRunnable)
    }
}
