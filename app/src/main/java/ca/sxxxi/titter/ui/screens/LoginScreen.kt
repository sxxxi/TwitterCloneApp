package ca.sxxxi.titter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.sxxxi.titter.R
import ca.sxxxi.titter.ui.components.FieldValidationStatus
import ca.sxxxi.titter.ui.components.PasswordInput
import ca.sxxxi.titter.ui.components.TextInput
import ca.sxxxi.titter.ui.theme.TitterTheme
import ca.sxxxi.titter.ui.viewmodels.LoginViewModel
import ca.sxxxi.titter.ui.viewmodels.LoginViewModel.LoginUiState.LoginStatus

@Composable
fun LoginScreen(
	uiState: LoginViewModel.LoginUiState = LoginViewModel.LoginUiState(),
	onInputChanged: (username: String, password: String) -> Unit = { _, _ -> },
	onLoginRequest: () -> Unit = {},
	checkUserIsLoggedIn: () -> Unit = {},
	onNavigateToSignup: () -> Unit = {},
	onNavigateToHome: () -> Unit = {}
) {
	LaunchedEffect(key1 = Unit) {
		checkUserIsLoggedIn()
	}

	Surface(
		Modifier
			.background(MaterialTheme.colorScheme.background)
			.fillMaxSize()
			.padding(horizontal = 16.dp)
	) {
		LaunchedEffect(key1 = uiState.status) {
			when (uiState.status) {
				is LoginStatus.Authorized -> onNavigateToHome()
				else -> {}
			}
		}

		when (uiState.status) {
			LoginStatus.Edit, is LoginStatus.Unauthorized -> {
				EditState(
					onInputChanged = onInputChanged,
					onLoginRequest = onLoginRequest,
					onNavigateToSignup = onNavigateToSignup,
					uiState = uiState
				)
			}

			else -> {
				LoadingState()
			}
		}

	}
}

@Composable
private fun EditState(
	uiState: LoginViewModel.LoginUiState,
	onInputChanged: (username: String, password: String) -> Unit,
	onLoginRequest: () -> Unit,
	onNavigateToSignup: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Icon(
			painter = painterResource(id = R.drawable.ic_launcher_foreground),
			contentDescription = null
		)

		InputFields(
			uiState = uiState,
			onInputChanged = onInputChanged
		)
		when (uiState.status) {
			is LoginStatus.Unauthorized -> {
				ErrorDisplay(
					modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
					message = uiState.status.message
				)
			}

			else -> {
				Spacer(modifier = Modifier.height(16.dp))
			}
		}
		ButtonRow(
			onNavigateToSignup = onNavigateToSignup,
			onLoginRequest = onLoginRequest
		)
	}
}

@Composable
private fun ErrorDisplay(
	modifier: Modifier = Modifier,
	message: String
) {
	val style = MaterialTheme.typography.bodyMedium + TextStyle(color = Color.Red)
	if (message.isNotEmpty()) Text(modifier = modifier, text = message, style = style)
}

@Composable
private fun InputFields(
	modifier: Modifier = Modifier,
	uiState: LoginViewModel.LoginUiState,
	onInputChanged: (username: String, password: String) -> Unit
) {
	var username by rememberSaveable {
		mutableStateOf("")
	}
	var pw by rememberSaveable {
		mutableStateOf("")
	}

	val validationColor = when (uiState.status) {
		is LoginStatus.Unauthorized -> {
			FieldValidationStatus.Invalid()
		}

		else -> FieldValidationStatus.Neutral
	}

	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		TextInput(
			label = "Username",
			value = username,
			validationStatus = validationColor,
			onValueChange = {
				username = it
				onInputChanged(username, pw)
			},
		)

		PasswordInput(
			label = "Password",
			value = pw,
			validationStatus = validationColor,
			onValueChange = {
				pw = it
				onInputChanged(username, pw)
			}
		)
	}
}

@Composable
private fun ButtonRow(
	onNavigateToSignup: () -> Unit,
	onLoginRequest: () -> Unit
) {
	Row {
		FilledTonalButton(onClick = onNavigateToSignup) {
			Text(
				modifier = Modifier.padding(8.dp),
				text = "Signup",
			)
		}

		Spacer(modifier = Modifier.width(8.dp))

		Button(
			onClick = onLoginRequest
		) {
			Text(
				modifier = Modifier.padding(8.dp),
				text = "Login",
				style = MaterialTheme.typography.bodyMedium
			)
		}
	}
}

@Composable
private fun LoadingState() {
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(text = "Loading...")
	}
}

@Preview
@Composable
fun PreviewLoginScreen() {
	TitterTheme {
		LoginScreen(uiState = LoginViewModel.LoginUiState(status = LoginStatus.Edit))
	}

}