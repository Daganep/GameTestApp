package com.neway.gametestapp.ui.howto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.neway.gametestapp.R

/**
 * "How to Play" screen (Stage 3.4).
 *
 * Explains the game rules: controls (swipe / arrow keys), food and scoring,
 * collision and the speed increase, with a "Back" button to return to the menu.
 */
@Composable
fun HowToScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.menu_how_to),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Rule(stringResource(R.string.rule_steer))
        Rule(stringResource(R.string.rule_food))
        Rule(stringResource(R.string.rule_collision))
        Rule(stringResource(R.string.rule_speed))

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(onClick = onBack) {
            Text(text = stringResource(R.string.back))
        }
    }
}

@Composable
private fun Rule(text: String) {
    Text(
        text = "•  $text",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}
