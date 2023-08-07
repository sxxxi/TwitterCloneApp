package ca.sxxxi.titter.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import ca.sxxxi.titter.Screen
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.ui.screens.CommentsScreen
import ca.sxxxi.titter.ui.viewmodels.CommentsViewModel

fun NavGraphBuilder.commentsScreen(onBackPressed: () -> Unit) {
	composable(
		route = Screen.CommentsScreen.routeWithArgs,
		arguments = Screen.CommentsScreen.arguments
	) {
		val commentsViewModel: CommentsViewModel = hiltViewModel()
		val uiState by commentsViewModel.uiState.collectAsStateWithLifecycle()
		CommentsScreen(
			uiState = uiState,
			onBackPressed = onBackPressed,
			repliesLoader = commentsViewModel::getCommentReplies
		)
	}
}

fun NavController.navigateToCommentsScreen(postId: String) {
	navigate(Screen.CommentsScreen.route + "/$postId") {

	}
}

internal class CommentsScreenArgs(savedStateHandle: SavedStateHandle) {
	val postId: String

	init {
		postId = checkNotNull(savedStateHandle[POST_ID_ARG])
	}
	companion object {
		const val POST_ID_ARG = "postId"
	}
}