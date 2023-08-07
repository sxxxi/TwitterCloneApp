package ca.sxxxi.titter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.sxxxi.titter.Screen
import ca.sxxxi.titter.ui.screens.PostCreateScreen
import ca.sxxxi.titter.ui.viewmodels.PostCreateViewModel

fun NavGraphBuilder.postCreate(
	onExitRequested: () -> Unit
) {
	composable(route = Screen.PostCreate.route, arguments = Screen.PostCreate.arguments) {
		val postCreateViewModel = hiltViewModel<PostCreateViewModel>()
		val uiState by postCreateViewModel.uiState.collectAsStateWithLifecycle()
		PostCreateScreen(
			uiState = uiState,
			onInputChange = postCreateViewModel::updatePost,
			onPostClicked = postCreateViewModel::newPost,
			onExitRequested = onExitRequested
		)
	}
}

fun NavController.navigateToPostCreate() {
	navigate(Screen.PostCreate.route)
}