package com.neway.gametestapp.game.storage

/**
 * Abstraction for persisting the best score.
 * The contract is defined in Stage 2 (2.1).
 */
interface ScoreStorage {
    fun getBest(): Int

    fun updateBest(score: Int)
}
