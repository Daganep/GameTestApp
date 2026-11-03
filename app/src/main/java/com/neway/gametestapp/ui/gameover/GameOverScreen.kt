package com.neway.gametestapp.ui.gameover

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neway.gametestapp.R

/**
 * Full-screen "Game Over" overlay shown on top of [com.neway.gametestapp.ui.game.GameScreen].
 *
 * @param score        final score of the finished round.
 * @param isNewRecord  true when [score] exceeds the previously stored best score.
 * @param onRestart    called when the player taps "Play Again".
 * @param onExit       called when the player taps "Menu".
 */
@Composable
fun GameOverScreen(
    score: Int,
    isNewRecord: Boolean = false,
    onRestart: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    // Пункт 6.1 — анимация появления: контент плавно проявляется и слегка увеличивается.
    val appear by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "gameover_appear"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .alpha(appear)
                .scale(0.92f + 0.08f * appear),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.game_over),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.score_label, score),
                fontSize = 22.sp,
                color = Color.White
            )

            if (isNewRecord) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.new_record),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(onClick = onRestart) {
                Text(stringResource(R.string.menu_play_again))
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(onClick = onExit) {
                Text(stringResource(R.string.menu_back))
            }
        }
    }
}
