package com.neway.gametestapp.game.model

/**
 * Game tuning configuration.
 * Defaults follow the values defined in Stage 1 (1.1).
 */
data class GameConfig(
    val size: Int = 15,
    val startIntervalMs: Long = 150L,
    val minIntervalMs: Long = 60L,
    val speedUpEvery: Int = 5,
    val intervalStepMs: Long = 10L
)
