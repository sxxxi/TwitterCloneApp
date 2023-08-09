package ca.sxxxi.titter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.sxxxi.titter.data.network.models.forms.PostCreateForm
import ca.sxxxi.titter.ui.theme.TitterTheme
import ca.sxxxi.titter.ui.viewmodels.PostCreateViewModel.PostCreateUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreateScreen(
	uiState: PostCreateUiState = PostCreateUiState(),
	onInputChange: ((PostCreateForm) -> PostCreateForm) -> Unit = {},
	onExitRequested: () -> Unit = {},
	onPostClicked: () -> Unit = {},
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surface)

	) {
		TopAppBar(
			navigationIcon = {
				IconButton(onClick = { onExitRequested() }) {
					Icon(imageVector = Icons.Default.Close, contentDescription = null)
				}
			},
			title = { Text(text = "New Post") }
		)

		BasicTextField(
			modifier = Modifier
				.fillMaxSize()
				.weight(1f)
				.padding(horizontal = 24.dp),
			cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
			textStyle = MaterialTheme.typography.bodyLarge + TextStyle(color = MaterialTheme.colorScheme.onSurface),
			value = uiState.newPost.content,
			onValueChange = { newContent ->
				onInputChange { postEntity ->
					postEntity.copy(content = newContent)
				}
			},
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			IconButton(onClick = { /*TODO*/ }) {
				Icon(imageVector = Icons.Default.Person, contentDescription = null)
			}
			IconButton(onClick = { /*TODO*/ }) {
				Icon(imageVector = Icons.Default.Person, contentDescription = null)
			}
			IconButton(onClick = { /*TODO*/ }) {
				Icon(imageVector = Icons.Default.Person, contentDescription = null)
			}

			Spacer(modifier = Modifier.weight(1f))

			Button(
				onClick = {
					onPostClicked()
					onExitRequested()
				}
			) {
				Text(text = "Post")
			}

		}
	}
}


@Preview
@Composable
fun PreviewPostCreateScreen() {
	TitterTheme {
		Surface(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			PostCreateScreen()
		}
	}
}
