package ca.sxxxi.titter.ui.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import ca.sxxxi.titter.activeUser.ActiveUser
import ca.sxxxi.titter.data.models.Comment
import ca.sxxxi.titter.data.models.CommentReplyPage
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.network.CommentsNetworkDataSource
import ca.sxxxi.titter.data.network.models.forms.CommentCreateForm
import ca.sxxxi.titter.data.paging.CommentsPagingSource
import ca.sxxxi.titter.data.repositories.post.CommentRepository
import ca.sxxxi.titter.data.repositories.post.PostRepository
import ca.sxxxi.titter.data.repositories.user.AuthenticationRepository
import ca.sxxxi.titter.data.utils.contracts.CommentMapper
import ca.sxxxi.titter.data.utils.states.Status
import ca.sxxxi.titter.ui.navigation.CommentsScreenArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val postRepositoryImpl: PostRepository,
	private val commentsNetworkDataSource: CommentsNetworkDataSource,
	private val authenticationRepository: AuthenticationRepository,
	private val commentMapper: CommentMapper,
	private val commentRepository: CommentRepository
) : ViewModel() {
	private val args = CommentsScreenArgs(savedStateHandle)
	private var _uiState = MutableStateFlow(CommentsUiState())
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			authenticationRepository.activeUser.first().let { activeUser ->
				_uiState.update { commentsUiState -> commentsUiState.copy(activeUser = activeUser) }
			}

			// Get post to display
			postRepositoryImpl.getPostById(args.postId).let { post ->
				_uiState.update { state ->
					state.copy(
						post = post,
						comments = commentsPager(args.postId)
					)
				}
				Log.i(TAG, "Post fetched: $post")
			}
		}
	}

	fun updateRecipient(recipient: Comment?) {
		// update comment create recipient
		_uiState.update {
			it.copy(
				commentCreateForm = it.commentCreateForm.copy(
					recipientId = recipient?.id,
					content = ""
				),
				commentRecipient = recipient
			)
		}
	}


	private fun commentsPager(postId: String): Flow<PagingData<Comment>> {
		return Pager(config = PagingConfig(pageSize = 1)) {
			CommentsPagingSource(
				commentMapper = commentMapper,
				postId = postId,
				commentsNetSource = commentsNetworkDataSource,
				authenticationRepository = authenticationRepository
			)
		}.flow.cachedIn(viewModelScope)
	}

	fun getCommentReplies(
		commentId: String,
		commentsReplyStore: Map<String, List<CommentReplyPage>>,
		depth: Int = 2
	) {
		// get comment replies
		viewModelScope.launch {
			commentRepository.getCommentRepliesById(
				id = commentId,
				commentsMap = commentsReplyStore,
				depth = depth
			)?.let { newCommentReplyPage ->
				// Update uiState
				val replyStoreCopy = mutableMapOf<String, List<CommentReplyPage>>()
				replyStoreCopy.putAll(commentsReplyStore)

				val oldList = commentsReplyStore[commentId] ?: listOf()

				val newList = listOf(newCommentReplyPage)
				newList.plus(oldList)

				if (replyStoreCopy.contains(commentId)) {
					replyStoreCopy.replace(commentId, newList)
				} else {
					replyStoreCopy[commentId] = newList
				}

				_uiState.update { it.copy(commentRepliesStore = replyStoreCopy.toMap()) }
			}
		}
	}

	fun postComment() {
		viewModelScope.launch {
			// Gross
			uiState.value.let { state ->
				_uiState.update { it.copy(commentCreateStatus = Status.Loading) }
				if (state.post != null && state.activeUser != null) {
					commentRepository.postComment(
						postId = state.post.id.toString(),
						jwt = state.activeUser.key,
						commentCreateForm = state.commentCreateForm
					).onSuccess {
						_uiState.update { it.copy(
							commentCreateStatus = Status.Success,
							commentCreateForm = CommentCreateForm(content = ""),
							commentRecipient = null
						) }
					}.onFailure {
						_uiState.update { it.copy(commentCreateStatus = Status.Failure) }
					}
				} else {
					_uiState.update { it.copy(commentCreateStatus = Status.Failure) }
				}
			}
		}
	}

	fun editComment(comment: String) {
		val commentForm = uiState.value.commentCreateForm.copy(content = comment)
		_uiState.update {
			it.copy(
				commentCreateForm = commentForm,
				commentCreateStatus = Status.Neutral
			)
		}
	}

	data class CommentsUiState(
		val activeUser: ActiveUser? = null,
		val post: Post? = null,
		val comments: Flow<PagingData<Comment>> = flow { },
		val commentRepliesStore: Map<String, List<CommentReplyPage>> = mutableMapOf(),
		val commentCreateForm: CommentCreateForm = CommentCreateForm(content = ""),
		val commentCreateStatus: Status = Status.Neutral,
		val commentRecipient: Comment? = null
	)

	companion object {
		const val TAG = "CommentsViewModel"
	}
}