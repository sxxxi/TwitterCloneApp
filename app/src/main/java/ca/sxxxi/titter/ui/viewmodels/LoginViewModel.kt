package ca.sxxxi.titter.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sxxxi.titter.data.network.models.forms.LoginRequest
import ca.sxxxi.titter.data.repositories.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
	private val authRepository: AuthenticationRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(LoginUiState())
	val uiState = _uiState.asStateFlow()

	fun requestLogin() {
		viewModelScope.launch(Dispatchers.IO) {
			val req = uiState.value.let { u ->
				LoginRequest(u.username, u.password)
			}
			updateStatus(LoginUiState.LoginStatus.Loading)
			authRepository.authenticate(req).onFailure {
				updateStatus(LoginUiState.LoginStatus.Unauthorized(it.message ?: "Error"))
			}.onSuccess {
				updateStatus(LoginUiState.LoginStatus.Authorized)
			}
		}
	}

	fun updateInput(username: String, password: String) {
		_uiState.update {
			it.copy(username = username, password = password)
		}
	}

	fun checkActiveUser() {
		viewModelScope.launch {
			authRepository.activeUser.collectLatest { activeUser ->
				val state = if (activeUser.id.isNotEmpty()) {
					LoginUiState.LoginStatus.Authorized
				} else {
					LoginUiState.LoginStatus.Edit
				}
				updateStatus(state)
			}
		}
	}

	private fun updateStatus(status: LoginUiState.LoginStatus) {
		_uiState.update { it.copy(status = status) }
	}

	data class LoginUiState(
		val status: LoginStatus = LoginStatus.Loading,
		val username: String = "",
		val password: String = ""
	) {
		sealed class LoginStatus {
			object Authorized : LoginStatus()
			data class Unauthorized(val message: String = "") : LoginStatus()
			object Edit : LoginStatus()
			object Loading : LoginStatus()
		}
	}
}