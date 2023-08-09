package ca.sxxxi.titter.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import ca.sxxxi.titter.activeUser.ActiveUser
import ca.sxxxi.titter.data.models.UserSearchItem
import ca.sxxxi.titter.data.repositories.AuthenticationRepository
import ca.sxxxi.titter.data.repositories.ProfileRepository
import ca.sxxxi.titter.data.repositories.SearchRepository
import ca.sxxxi.titter.data.repositories.paging.UserSearchPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	private val authenticationRepository: AuthenticationRepository,
	private val profileRepository: ProfileRepository,
	private val searchRepository: SearchRepository
) : ViewModel() {
	private var _uiState = MutableStateFlow(SearchUiState())
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			authenticationRepository.activeUser.first().let { activeUser ->
				_uiState.update { it.copy(activeUser = activeUser) }
			}
		}
	}

	fun editSearchTerm(term: String) {
		_uiState.update { it.copy(searchTerm = term) }
	}

	fun searchUser(searchTerm: String) {
		uiState.value.activeUser?.key?.let { jwt ->
			if (jwt.isEmpty() || uiState.value.searchTerm.isEmpty()) return
			_uiState.update { state ->
				state.copy(
					userSearchResultsFlow = userSearchPager(
						searchTerm = searchTerm,
						jwt = jwt
					)
				)
			}
		}
	}

	fun followUser(userId: String, followState: MutableState<Boolean>) {
		viewModelScope.launch {
			uiState.value.activeUser?.key?.let { jwt ->
				profileRepository.followUser(jwt, userId).onSuccess {
					followState.value = true
				}
			}
		}
	}

	fun unfollowUser(userId: String, followState: MutableState<Boolean>) {
		viewModelScope.launch {
			uiState.value.activeUser?.key?.let { jwt ->
				profileRepository.unfollowUser(jwt, userId).onSuccess {
					followState.value = false
				}
			}
		}
	}

	private fun userSearchPager(searchTerm: String, jwt: String): Flow<PagingData<UserSearchItem>> {
		return Pager(config = PagingConfig(pageSize = 100)) {
			UserSearchPagingSource { page ->
				viewModelScope.async {
					searchRepository.searchUser(
						jwt = jwt,
						term = searchTerm,
						page = page,
						pageSize = 5,
						context = Dispatchers.IO
					)
				}
			}
		}.flow.cachedIn(viewModelScope)
	}

	data class SearchUiState(
		val activeUser: ActiveUser? = null,
		val searchTerm: String = "",
		val userSearchResultsFlow: Flow<PagingData<UserSearchItem>>? = null
	)
}