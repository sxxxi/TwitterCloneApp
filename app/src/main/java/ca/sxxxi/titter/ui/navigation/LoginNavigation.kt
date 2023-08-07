package ca.sxxxi.titter.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.sxxxi.titter.Screen
import ca.sxxxi.titter.ui.screens.LoginScreen
import ca.sxxxi.titter.ui.viewmodels.LoginViewModel

fun NavGraphBuilder.login(
	onNavigateToSignup: () -> Unit,
	onNavigateToHome: () -> Unit
) {
	composable(route = Screen.Login.route, arguments = Screen.Login.arguments) {
		val loginViewModel = hiltViewModel<LoginViewModel>()
		val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
		LoginScreen(
			uiState = uiState,
			onInputChanged = loginViewModel::updateInput,
			onLoginRequest = loginViewModel::requestLogin,
			checkUserIsLoggedIn = loginViewModel::checkActiveUser,
			onNavigateToSignup = onNavigateToSignup,
			onNavigateToHome = onNavigateToHome
		)
	}
}

fun NavController.navigateToLogin() {
	navigate(Screen.Login.route) {
		popUpTo(Screen.Login.route) {
			inclusive = true
			saveState = true
		}
	}
}