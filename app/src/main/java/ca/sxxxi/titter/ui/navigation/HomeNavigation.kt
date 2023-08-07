package ca.sxxxi.titter.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.sxxxi.titter.Screen
import ca.sxxxi.titter.di.HiltModule
import ca.sxxxi.titter.di.MapperModule
import ca.sxxxi.titter.ui.screens.HomeScreen
import ca.sxxxi.titter.ui.viewmodels.HomeViewModel

fun NavGraphBuilder.homeScreen(
	onNavigateToAuthentication: () -> Unit,
	onNavigateToPostCreate: () -> Unit,
	onNavigateToComments: (String) -> Unit
) {
	composable(route = Screen.Home.route, arguments = Screen.Home.arguments) {
		val homeViewModel = hiltViewModel<HomeViewModel>()
		val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
		HomeScreen(
			uiState = homeUiState,
			onPostCreateClicked = onNavigateToPostCreate,
			onLogout = homeViewModel::logout,
			onNavigateToAuthentication = onNavigateToAuthentication,
			onNavigateToComments = onNavigateToComments
		)
	}
}

fun NavController.navigateToHome() {
	navigate(Screen.Home.route) {
		popBackStack()
	}
}