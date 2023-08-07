package ca.sxxxi.titter.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.sxxxi.titter.data.local.entities.PostEntity
import ca.sxxxi.titter.data.local.entities.combine.PostWithUser
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.network.models.forms.PostCreateForm
import ca.sxxxi.titter.data.repositories.AuthenticationRepository
import ca.sxxxi.titter.data.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostCreateViewModel @Inject constructor(
	private val authRepo: AuthenticationRepository,
	private val postRepository: PostRepository,
) : ViewModel() {
	private val _uiState = MutableStateFlow(PostCreateUiState())
	val uiState = _uiState.asStateFlow()

	fun updatePost(transform: (PostCreateForm) -> PostCreateForm) {
		_uiState.update { it.copy(newPost = transform(it.newPost)) }
	}

	fun newPost() {
		viewModelScope.launch(Dispatchers.IO) {
			postRepository.sendPostToRemote(uiState.value.newPost)
		}
	}

	data class PostCreateUiState(
		val newPost: PostCreateForm = PostCreateForm()
	)
}