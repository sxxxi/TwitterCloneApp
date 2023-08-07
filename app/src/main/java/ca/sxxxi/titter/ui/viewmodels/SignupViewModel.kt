package ca.sxxxi.titter.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sxxxi.titter.data.network.models.forms.SignupRequest
import ca.sxxxi.titter.data.repositories.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
	private val authRepo: AuthenticationRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow(SignupUiState())
	val uiState = _uiState.asStateFlow()

	fun requestSignup() {
		viewModelScope.launch {
			updateStatusState(SignupUiState.RegistrationStatus.Loading)
			authRepo.signup(uiState.value.newUser).let { res ->
				updateStatusState(
					if (res.isSuccess)
						SignupUiState.RegistrationStatus.Success
					else
						SignupUiState.RegistrationStatus.Error(res.exceptionOrNull()?.message ?: "Error")
				)
			}
		}
	}

	fun updateUserState(transform: (SignupRequest) -> SignupRequest) {
		_uiState.update {
			it.copy(newUser = transform(it.newUser.copy()))
		}
	}

	private fun updateStatusState(status: SignupUiState.RegistrationStatus) {
		_uiState.update { state ->
			state.copy(status = status)
		}
	}

	data class SignupUiState(
		val status: RegistrationStatus = RegistrationStatus.Edit,
		val newUser: SignupRequest = SignupRequest(),
	) {
		sealed interface RegistrationStatus {
			object Success : RegistrationStatus
			object Edit : RegistrationStatus
			class Error(val message: String) : RegistrationStatus
			object Loading : RegistrationStatus
		}
	}

}