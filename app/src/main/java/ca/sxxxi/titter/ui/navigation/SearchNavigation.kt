package ca.sxxxi.titter.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.sxxxi.titter.Screen
import ca.sxxxi.titter.ui.screens.SearchScreen
import ca.sxxxi.titter.ui.viewmodels.SearchViewModel

fun NavGraphBuilder.searchScreen() {
	composable(
		route = Screen.SearchScreen.routeWithArgs,
		arguments = Screen.SearchScreen.arguments
	) {
		val viewModel = hiltViewModel<SearchViewModel>()
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()
		SearchScreen(
			uiState = uiState,
			onSearchTermEdit = viewModel::editSearchTerm,
			onSearch = viewModel::searchUser,
			onFollow = viewModel::followUser,
			onUnfollow = viewModel::unfollowUser
		)
	}
}

fun NavController.navigateToSearch() {
	navigate(Screen.SearchScreen.route)
}