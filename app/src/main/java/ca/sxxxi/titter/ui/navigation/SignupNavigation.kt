package ca.sxxxi.titter.ui.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.sxxxi.titter.Screen
import ca.sxxxi.titter.ui.screens.SignupScreen
import ca.sxxxi.titter.ui.viewmodels.SignupViewModel

fun NavGraphBuilder.signup(onNavigateToLogin: () -> Unit) {
	composable(
		route = Screen.Signup.route,
		arguments = Screen.Signup.arguments
	) {
		val signupViewModel = hiltViewModel<SignupViewModel>()
		val uiState by signupViewModel.uiState.collectAsStateWithLifecycle()
		SignupScreen(
			uiState = uiState,
			onInputChange = signupViewModel::updateUserState,
			onSignupRequest = signupViewModel::requestSignup,
			onNavigateToLogin =	onNavigateToLogin
		)
	}
}

fun NavController.navigateToSignup() {
	navigate(Screen.Signup.route) {
		popUpTo(Screen.Signup.route) {
			inclusive = true
			saveState = true
		}
	}
}