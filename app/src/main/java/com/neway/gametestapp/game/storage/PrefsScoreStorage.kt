package com.neway.gametestapp.game.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * [ScoreStorage] backed by [SharedPreferences].
 *
 * Prefs file: [PREFS_FILE], key: [KEY_BEST_SCORE].
 * A missing key is treated as a best score of 0.
 * A score is stored only when it is higher than the current best one.
 */
class PrefsScoreStorage(context: Context) : ScoreStorage {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    override fun getBest(): Int = prefs.getInt(KEY_BEST_SCORE, DEFAULT_BEST)

    override fun updateBest(score: Int) {
        if (score > getBest()) {
            prefs.edit().putInt(KEY_BEST_SCORE, score).apply()
        }
    }

    companion object {
        private const val PREFS_FILE = "snake_prefs"
        private const val KEY_BEST_SCORE = "best_score"
        private const val DEFAULT_BEST = 0
    }
}