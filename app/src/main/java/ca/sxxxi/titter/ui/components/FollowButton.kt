package ca.sxxxi.titter.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun FollowButton(
	modifier: Modifier = Modifier,
	followed: Boolean,
	userId: String,
	onFollow: (String, MutableState<Boolean>) -> Unit,
	onUnfollow: (String, MutableState<Boolean>) -> Unit
) {
	val followState = remember { mutableStateOf(followed) }
	if (followState.value) {
		FilledTonalButton(
			modifier = modifier,
			onClick = { onUnfollow(userId, followState) }
		) {
			Text(text = "Unfollow")
		}
	} else {
		Button(
			modifier = modifier,
			onClick = { onFollow(userId, followState) }
		) {
			Text(text = "Follow")
		}
	}
}