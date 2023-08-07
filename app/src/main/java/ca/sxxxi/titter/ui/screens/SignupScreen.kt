package ca.sxxxi.titter.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.sxxxi.titter.data.network.models.forms.SignupRequest
import ca.sxxxi.titter.ui.components.FieldValidationStatus
import ca.sxxxi.titter.ui.components.PasswordInput
import ca.sxxxi.titter.ui.components.TextInput
import ca.sxxxi.titter.ui.theme.TitterTheme
import ca.sxxxi.titter.ui.viewmodels.SignupViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SignupScreen(
	uiState: SignupViewModel.SignupUiState = SignupViewModel.SignupUiState(),
	onInputChange: ((SignupRequest) -> SignupRequest) -> Unit = {},
	onSignupRequest: () -> Unit = {},
	onNavigateToLogin: () -> Unit = {}
) {

	val coroutineScope = rememberCoroutineScope()

	// Navigate to login when signup succeeds
	LaunchedEffect(key1 = uiState.status) {
		when (uiState.status) {
			is SignupViewModel.SignupUiState.RegistrationStatus.Success -> {
				onNavigateToLogin()
			}
			else -> {}
		}
	}

	Column(
		Modifier.padding(horizontal = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		PfpPicker()
		InputFields(
			uiState = uiState,
			onInputChange = onInputChange,
			scope = coroutineScope
		)
		FormButtons(
			onNavigateToLogin = onNavigateToLogin,
			onSignupRequest = onSignupRequest
		)

	}
}

/**
 * Update view model state in the provided lambda
 */
@Composable
private fun PfpPicker(
	modifier: Modifier = Modifier,
	onPfpChanged: (ImageBitmap) -> Unit = {}
) {
	val ctx = LocalContext.current
	var pfp by remember { mutableStateOf<ImageBitmap?>(null) }
	val imagePickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia(),
		onResult = {
			it?.let { uri ->
				ctx.contentResolver.openInputStream(uri).use {st ->
					st?.let { imageStream ->
						val imageBytes = imageStream.readBytes()
						val imgBitmap = BitmapFactory
							.decodeByteArray(imageBytes, 0, imageBytes.size)
							.asImageBitmap()
						pfp = imgBitmap
						onPfpChanged(imgBitmap)
					}
				}
			}
		}
	)

	// Photo picker
	Column(
		modifier = modifier
			.padding(top = 48.dp, bottom = 24.dp)
			.clip(RoundedCornerShape(100))
			.border(
				width = 2.dp,
				color = MaterialTheme.colorScheme.primary,
				shape = RoundedCornerShape(100)
			)
			.background(MaterialTheme.colorScheme.secondary)
			.clickable {
				imagePickerLauncher.launch(
					PickVisualMediaRequest(
						ActivityResultContracts.PickVisualMedia.ImageOnly
					)
				)
			}
			.size(124.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		pfp?.let { img ->
			Image(bitmap = img, contentDescription = null, contentScale = ContentScale.Crop)
		} ?: Icon(imageVector = Icons.Default.Add, contentDescription = null)

	}
}

private sealed class SignupError(override val message: String = "") : Exception(message) {
	object RequiredFieldNotFilled : SignupError()
	object UsernameTaken : SignupError()
	object PasswordsNotMatching : SignupError()
}

@Composable
private fun InputFields(
	modifier: Modifier = Modifier,
	uiState: SignupViewModel.SignupUiState,
	onInputChange: ((SignupRequest) -> SignupRequest) -> Unit,
	scope: CoroutineScope,
	onFocusOut: () -> Unit = {}
) {
	var validationColor: FieldValidationStatus = FieldValidationStatus.Neutral
	val form = uiState.newUser

	val clickable: Boolean by remember { mutableStateOf(false) }
	var usernameValid: Boolean by remember { mutableStateOf(false) }
	var firstNameValid: Boolean by remember { mutableStateOf(false) }
	var lastNameValid: Boolean by remember { mutableStateOf(false) }
	var passwordValid: Boolean by remember { mutableStateOf(false) }

	var passwordState: String by remember { mutableStateOf("") }
	var repeatPasswordState: String by remember { mutableStateOf("") }

	LaunchedEffect(key1 = uiState.status) {
		validationColor = when (uiState.status) {
			is SignupViewModel.SignupUiState.RegistrationStatus.Error -> {
				FieldValidationStatus.Invalid()
			}
			else -> FieldValidationStatus.Neutral
		}
	}

	suspend fun errorFinder() = withContext(Dispatchers.Default) {
		try {
			// Validate username
			if (form.username.isEmpty()) {
				usernameValid = false
			}

			if (form.firstName.isEmpty()) {

				firstNameValid = false
			}

			if (form.lastName.isEmpty()) {
				lastNameValid = false
			}

			// Password and repeat must be filled and matching





		} catch (e: Exception) {
			// If
		}


	}

	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TextInput(
			label = "Username",
			value = uiState.newUser.username,
			onValueChange = { newId ->
				onInputChange {
					it.copy(username = newId)
				}
			},
			onFocusOut = onFocusOut
		)
		TextInput(
			label = "First Name",
			value = uiState.newUser.firstName,
			validationStatus = validationColor,
			onValueChange = { newFirst ->
				onInputChange {
					it.copy(firstName = newFirst)
				}
			},
			onFocusOut = onFocusOut
		)
		TextInput(
			label = "Last Name",
			value = uiState.newUser.lastName,
			onValueChange = { newLast ->
				onInputChange {
					it.copy(lastName = newLast)
				}
			},
			onFocusOut = onFocusOut
		)
		PasswordInput(
			label = "Password",
			value = form.password,
			onValueChange = { newVal ->
				onInputChange {
					it.copy(password = newVal)
				}
			},
			onFocusOut = onFocusOut
		)
//		PasswordInput(
//			value = repeatPasswordState,
//			onValueChange = {
//				repeatPasswordState = it
//			},
//			onFocusOut = onFocusOut
//		)
	}

}

@Composable
private fun FormButtons(
	modifier: Modifier = Modifier,
	onNavigateToLogin: () -> Unit = {},
	onSignupRequest: () -> Unit = {}
) {
	Row {
		FilledTonalButton(onClick = onNavigateToLogin) {
			Text(
				modifier = Modifier.padding(8.dp),
				text = "Login",
			)
		}

		Spacer(modifier = Modifier.width(8.dp))

		Button(
			onClick = {
				onSignupRequest()
			}
		) {
			Text(
				modifier = Modifier.padding(8.dp),
				text = "Signup",
			)
		}
	}
}

@Preview
@Composable
fun PreviewSignupScreen() {
	TitterTheme {
		Surface(Modifier.fillMaxSize()) {
			SignupScreen()
		}
	}
}