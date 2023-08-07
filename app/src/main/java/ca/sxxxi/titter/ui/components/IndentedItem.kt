package ca.sxxxi.titter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Indents the content and draws a vertical line on the left side of the content.
 * Mainly used for flat list to simulate a tree like structure.
 * <Inspired by Reddit comments section>
 */
@Composable
fun IndentedItem(
	depth: Int,
	indentPadding: Dp = 16.dp,
	content: @Composable () -> Unit
) {
	val lineColor = MaterialTheme.colorScheme.onSurfaceVariant
	val startPad = remember { indentPadding * depth }
	Box(
		modifier = Modifier
			.offset(x = startPad)
			.drawBehind {
				for (i in 0 until depth) {
					// Draw line connecting to the parent's line
					val offsetX = -(indentPadding * i).toPx()
					drawLine(
						color = lineColor,
						start = Offset(offsetX, 0f),
						end = Offset(offsetX, size.height)
					)
				}
			}
	) {
		content()
	}
}