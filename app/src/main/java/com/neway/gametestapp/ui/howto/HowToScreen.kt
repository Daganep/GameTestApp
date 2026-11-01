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
            text = "How to Play",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Rule("Steer the snake with swipe gestures or arrow keys.")
        Rule("Eat the food to grow — every food gives +10 points.")
        Rule("Hit a wall or your own body and the game is over.")
        Rule("The snake gets a little faster with every food.")

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(onClick = onBack) {
            Text(text = "Back")
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
