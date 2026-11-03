package com.neway.gametestapp.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neway.gametestapp.BuildConfig
import com.neway.gametestapp.R
import com.neway.gametestapp.ui.theme.GameBackground
import com.neway.gametestapp.ui.theme.GameSurface
import com.neway.gametestapp.ui.theme.SnakeGreen
import com.neway.gametestapp.ui.theme.TextPrimary

@Composable
fun MainMenuScreen(
    onPlay: () -> Unit,
    onHowTo: () -> Unit,
    bestScore: Int
) {
    val version = BuildConfig.VERSION_NAME
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F1A0F), GameBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.game_title),
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                color = SnakeGreen,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.best_score, bestScore),
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = TextPrimary.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SnakeGreen),
            ) {
                Text(stringResource(R.string.menu_play), fontSize = 20.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onHowTo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GameSurface),
            ) {
                Text(stringResource(R.string.menu_how_to), fontSize = 18.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(72.dp))
            Text(
                text = stringResource(R.string.version_copyright, version),
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = TextPrimary.copy(alpha = 0.4f),
            )
        }
    }
}