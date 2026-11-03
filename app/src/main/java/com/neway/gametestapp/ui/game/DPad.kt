package com.neway.gametestapp.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neway.gametestapp.game.model.Direction

private val BUTTON_SIZE = 64.dp
private val GAP = 8.dp

/**
 * A cross-shaped directional pad with four arrow buttons.
 *
 * Layout:
 * ```
 *   [  ]  [↑]  [  ]
 *   [←]  [ ]  [→]
 *   [  ]  [↓]  [  ]
 * ```
 *
 * @param onDirection called with the pressed [Direction].
 * @param modifier applied to the outer [Column].
 */
@Composable
fun DPad(
    onDirection: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GAP)
    ) {
        // Row 1: _  ↑  _
        Row(horizontalArrangement = Arrangement.spacedBy(GAP)) {
            Spacer(Modifier.size(BUTTON_SIZE))
            DPadButton("↑") { onDirection(Direction.UP) }
            Spacer(Modifier.size(BUTTON_SIZE))
        }
        // Row 2: ←  _  →
        Row(horizontalArrangement = Arrangement.spacedBy(GAP)) {
            DPadButton("←") { onDirection(Direction.LEFT) }
            Spacer(Modifier.size(BUTTON_SIZE))
            DPadButton("→") { onDirection(Direction.RIGHT) }
        }
        // Row 3: _  ↓  _
        Row(horizontalArrangement = Arrangement.spacedBy(GAP)) {
            Spacer(Modifier.size(BUTTON_SIZE))
            DPadButton("↓") { onDirection(Direction.DOWN) }
            Spacer(Modifier.size(BUTTON_SIZE))
        }
    }
}

@Composable
private fun DPadButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(BUTTON_SIZE),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(text = text, fontSize = 28.sp)
    }
}

