package ca.sxxxi.titter.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sxxxi.titter.data.network.CommentsNetworkDataSource
import ca.sxxxi.titter.data.network.PostNetworkDataSource
import ca.sxxxi.titter.data.prefs.UserPreferences
import ca.sxxxi.titter.data.repositories.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TestingViewModel @Inject constructor(
	private val postNetworkDataSource: PostNetworkDataSource,
	private val userPreferences: UserPreferences,
	private val commentsNetworkDataSource: CommentsNetworkDataSource,
	private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
	val uiState = MutableStateFlow(TestingUiState())

	init {
		viewModelScope.launch {
			authenticationRepository.activeUser.combine(userPreferences.latestRefreshDate) { a, r ->
				uiState.value.copy(token = a.key, latestRefresh = r)
			}.collectLatest {  uiStateCopy ->
				uiState.update { uiStateCopy }
			}
		}
	}

	fun getClosestPost() {
		viewModelScope.launch(Dispatchers.IO) {
			userPreferences.refresh()
			Log.d("BOOBAR", "${uiState.value.token}")
			uiState.value.token?.let { token ->
				postNetworkDataSource.getUserFeed(
					token = token,
					from = uiState.value.latestRefresh,
					pageSize = 2,
					page = 0
				).await().content.firstOrNull()?.let { post ->
					uiState.update { us -> us.copy(fetchedPost = post.id) }
				}
			}
			Log.d("BOOBAR", "${uiState.value.fetchedPost}")
		}
	}

	fun test() {
		viewModelScope.launch(Dispatchers.IO) {
			uiState.value.token?.let { token ->
				uiState.value.fetchedPost?.let {
					commentsNetworkDataSource.getPostComments(
						token = token,
						postId = it,
						depth = 2,
						pageSize = 3
					).await().let {
						Log.d("BOOBAR", "$it")
					}
				}
			}
		}
	}

	data class TestingUiState(
		val token: String? = null,
		val latestRefresh: Long = 0L,
		val fetchedPost: String? = null
	)
}