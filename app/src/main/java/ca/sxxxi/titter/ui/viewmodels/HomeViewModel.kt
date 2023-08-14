package ca.sxxxi.titter.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import ca.sxxxi.titter.data.local.entities.combine.PostWithUser
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.models.User
import ca.sxxxi.titter.data.repositories.post.PostRepository
import ca.sxxxi.titter.data.repositories.user.AuthenticationRepository
import ca.sxxxi.titter.data.repositories.post.PostRepositoryImpl
import ca.sxxxi.titter.data.utils.contracts.PostMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val authRepo: AuthenticationRepository,
	private val postsRepo: PostRepository,
	private val postMapper: PostMapper,
	pager: Pager<Int, PostWithUser>,
) : ViewModel() {
	private val _uiState = MutableStateFlow(
		HomeUiState(postPageDataFlow = pager.flow
			.cachedIn(viewModelScope)
			.map { pd ->
				pd.map { postMapper.entityToDomain(it) }
			}
		)
	)
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch(Dispatchers.IO) {
			authRepo.activeUser.collect { activeUser ->
				val user: User? = if (activeUser.id.isNotEmpty()) {
					User(
						id = activeUser.id,
						fName = activeUser.firstName,
						lName = activeUser.lastName
					)
				} else null

				_uiState.update{
					it.copy(activeUser = user)
				}
				Log.d("HomeViewModel", "${uiState.value.activeUser}")
			}
		}
	}

	fun getPosts() = viewModelScope.launch(Dispatchers.IO) {
		if (uiState.value.isLastPage) return@launch
		_uiState.update { it.copy(currentPage = it.currentPage + 1) }
		uiState.value.let { state ->
			// TODO: Do error handling later
			postsRepo.getPostPage(state.currentPage).onSuccess { newPage ->
				_uiState.update {
					it.copy(
						posts = it.posts + newPage.content,
						isLastPage = newPage.last
					)
				}
			}.onFailure {
				Log.e("BABABOOIE", "$it")
			}
		}
	}

	fun logout() {
		viewModelScope.launch(Dispatchers.IO) {
			authRepo.clear()
		}
	}

	data class HomeUiState(
		val activeUser: User? = User(),
		val latestPostDate: Long = 0L,
		val currentPage: Int = 1,
		val isLastPage: Boolean = false,
		val posts: List<Post> = listOf(),
		val pageIndex: Int = 0,
		val fetchRequested: Boolean = false,
		val postPageDataFlow: Flow<PagingData<Post>>
	)
}