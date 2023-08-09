package ca.sxxxi.titter.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.sxxxi.titter.data.models.UserSearchItem

@Composable
fun UserCardWithFollowButton(
	modifier: Modifier = Modifier,
	result: UserSearchItem,
	onFollow: (String, MutableState<Boolean>) -> Unit,
	onUnfollow: (String, MutableState<Boolean>) -> Unit
) {
	Row(
		modifier = Modifier
			.border(
				width = 1.dp,
				color = MaterialTheme.colorScheme.surfaceVariant,
				shape = RoundedCornerShape(20)
			)
			.padding(vertical = 24.dp)
			.padding(start = 32.dp, end = 24.dp)
			.then(modifier),
		verticalAlignment = Alignment.CenterVertically
	) {
		Column {
			Text(
				text = "${result.user.fName} ${result.user.lName}",
				style = MaterialTheme.typography.bodyLarge + TextStyle(fontWeight = FontWeight.Bold)
			)
			Text(
				text = result.user.id,
				style = MaterialTheme.typography.bodyMedium + TextStyle(color = MaterialTheme.colorScheme.secondary)
			)

		}
		Spacer(modifier = Modifier.weight(1f))

		FollowButton(
			followed = result.followed,
			userId = result.user.id,
			onFollow = onFollow,
			onUnfollow = onUnfollow
		)
	}
}