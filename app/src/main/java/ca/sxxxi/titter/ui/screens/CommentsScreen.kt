package ca.sxxxi.titter.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ca.sxxxi.titter.data.models.Comment
import ca.sxxxi.titter.data.models.CommentReplyPage
import ca.sxxxi.titter.data.utils.states.Status
import ca.sxxxi.titter.ui.components.IndentedItem
import ca.sxxxi.titter.ui.components.NewInput
import ca.sxxxi.titter.ui.components.PostCardWithoutButtons
import ca.sxxxi.titter.ui.components.RefreshablePagedList
import ca.sxxxi.titter.ui.viewmodels.CommentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
	uiState: CommentsViewModel.CommentsUiState,
	onBackPressed: () -> Unit = {},
	repliesLoader: (String, Map<String, List<CommentReplyPage>>, Int) -> Unit,
	onCommentEdit: (String) -> Unit,
	onCommentAdd: () -> Unit,
	onReplyRecipientChange: (comment: Comment?) -> Unit
) {
	val comments = uiState.comments.collectAsLazyPagingItems()
	val localFocusManager = LocalFocusManager.current
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(key1 = uiState.commentCreateStatus) {
		if (uiState.commentCreateStatus is Status.Success) {
			comments.refresh()
			onCommentEdit("")
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = "Comments") },
				colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = Color.Transparent),
				navigationIcon = {
					IconButton(onClick = onBackPressed) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back button"
						)
					}
				}
			)
		},
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) { pad ->
		AnimatedContent(targetState = uiState.post, label = "") { post ->
			val requester = remember { FocusRequester() }
			Column(
				Modifier
					.fillMaxSize()
					.padding(pad)
					.pointerInput(Unit) {
						detectTapGestures { localFocusManager.clearFocus() }
					},
			) {
				post?.let {
					PostCardWithoutButtons(post = it)
//					Column(
//						modifier = Modifier
//							.fillMaxWidth()
//					) {
//						Divider()
//						Text(
//							modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
//							text = "${comments.itemCount} Comments"
//						)
//					}
					Divider()
					CommentsList(
						modifier = Modifier.weight(1f),
						comments = comments,
						repliesStore = uiState.commentRepliesStore,
						repliesLoader = { commentId ->
							repliesLoader(
								commentId,
								uiState.commentRepliesStore,
								2
							)
						},
						onReplyRecipientChange = { recipientId ->
							requester.requestFocus()
							onReplyRecipientChange(recipientId)
						},
						snackbarHostState = snackbarHostState
					)
					CommentTextBox(
						modifier = Modifier.focusRequester(requester),
						comment = uiState.commentCreateForm.content,
						recipient = uiState.commentRecipient,
						onCommentEdit = onCommentEdit,
						onCommentAdd = onCommentAdd,
						commentStatus = uiState.commentCreateStatus,
						onReplyDismiss = {
							onReplyRecipientChange(null)
						}
					)
				}
			}
		}
	}
}

@Composable
fun CommentTextBox(
	modifier: Modifier = Modifier,
	comment: String,
	recipient: Comment? = null,
	onCommentAdd: () -> Unit = {},
	onCommentEdit: (String) -> Unit,
	commentStatus: Status,
	onReplyDismiss: () -> Unit
) {
	val bg = when (commentStatus) {
		is Status.Failure -> MaterialTheme.colorScheme.error
		else -> MaterialTheme.colorScheme.background
	}
	val bgBorder = MaterialTheme.colorScheme.surfaceVariant

	Box(
		modifier = Modifier
			.background(bg)
			.drawBehind {
				drawLine(
					color = bgBorder,
					start = Offset.Zero,
					end = Offset(x = this.size.width, y = 0f)
				)
			}
			.then(modifier)
	) {
		recipient?.let {
			val height = 40.dp

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(height)
					.offset(y = -height)
					.background(MaterialTheme.colorScheme.surface)
					.clip(RoundedCornerShape(100))
					.border(
						width = 1.dp,
						color = MaterialTheme.colorScheme.surfaceVariant,
						shape = RoundedCornerShape(100)
					),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					modifier = Modifier.padding(horizontal = 16.dp),
					text = "Replying to ${recipient.author.fName} ${recipient.author.lName}"
				)
				IconButton(onClick = onReplyDismiss) {
					Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear recipient")
				}
			}
		}
		Row(
			modifier = Modifier
				.padding(8.dp)
				.then(modifier),
			verticalAlignment = Alignment.CenterVertically
		) {
			NewInput(
				modifier = Modifier.weight(1f),
				value = comment,
				onValueChange = onCommentEdit,
			)
			if (commentStatus is Status.Loading) {
				CircularProgressIndicator()
			} else {
				IconButton(onClick = onCommentAdd) {
					Icon(imageVector = Icons.Default.Send, contentDescription = null)
				}
			}
		}
	}
}

@Composable
private fun CommentsList(
	modifier: Modifier = Modifier,
	comments: LazyPagingItems<Comment>,
	repliesStore: Map<String, List<CommentReplyPage>>,
	repliesLoader: (String) -> Unit,
	onReplyRecipientChange: (Comment) -> Unit,
	snackbarHostState: SnackbarHostState?
) {
	val snackBarHostState = remember { SnackbarHostState() }
	val isRefreshing = remember(comments.loadState.refresh) {
		comments.loadState.refresh !is LoadState.NotLoading
	}

	LaunchedEffect(key1 = comments.loadState.refresh) {
		when (comments.loadState.refresh) {
			is LoadState.Error -> {
				snackBarHostState.showSnackbar("Cannot fetch post comments.")
			}
			else -> {}
		}
	}

	Column(
		modifier = modifier,
	) {
		CommentsSection(
//			modifier = modifier,
			pagingData = comments,
			onRefresh = { comments.refresh() },
			repliesStore = repliesStore,
			repliesLoader = repliesLoader,
			isRefreshing = isRefreshing,
			onReplyRecipientChange = onReplyRecipientChange
		)
	}
}

@Composable
fun CommentsSection(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<Comment>,
	lazyListState: LazyListState = rememberLazyListState(),
	onRefresh: () -> Unit,
	repliesStore: Map<String, List<CommentReplyPage>>,
	repliesLoader: (String) -> Unit,
	isRefreshing: Boolean,
	onReplyRecipientChange: (Comment) -> Unit
) {
	RefreshablePagedList(
		modifier = modifier,
		pagingData = pagingData,
		refreshHandler = onRefresh,
		isRefreshing = isRefreshing,
		scrollState = lazyListState,
	) {
		commentNodes(
			comments = pagingData.itemSnapshotList.items,
			repliesStore = repliesStore,
			replyLoader = repliesLoader,
			onReplyRecipientChange = onReplyRecipientChange
		)
	}
}

@Composable
fun CommentCard(
	comment: Comment,
	onReplyRecipientChange: (Comment) -> Unit
) {
	AnimatedContent(targetState = comment, label = "") { animComment ->
		Box(modifier = Modifier.padding(vertical = 8.dp)) {
			Column(
				Modifier
					.padding(start = 20.dp)
					.padding(vertical = 12.dp)
					.width(300.dp)
			) {
				Text(
					text = "${animComment.author.fName} ${animComment.author.lName}",
					fontWeight = FontWeight.Bold,
					style = MaterialTheme.typography.bodySmall
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(text = animComment.content)
				TextButton(onClick = { onReplyRecipientChange(animComment) }) {
					Text(text = "Reply", style = MaterialTheme.typography.bodySmall)
				}
			}
		}
	}
}

private fun LazyListScope.commentNode(
	comment: Comment,
	repliesStore: Map<String, List<CommentReplyPage>>,
	depth: Int = 0,
	replyLoader: (commentId: String) -> Unit = {},
	onReplyRecipientChange: (Comment) -> Unit
) {
	var replies = comment.replies
	val commentHasAdditionalReplies = repliesStore.contains(comment.id)

	// if replies is null, check if it has replies in repliesStore
	// and update replies local variable value
	if (replies == null && commentHasAdditionalReplies) {
		val reps = mutableListOf<Comment>()
		repliesStore[comment.id]?.forEach { crp ->
			reps.addAll(crp.content)
		}
		replies = reps.toList()
	}

	item(key = comment.id) {
		Column {
			IndentedItem(
				depth = depth,
				indentPadding = 16.dp
			) {
				CommentCard(comment, onReplyRecipientChange)
			}
			// Replies == null indicates that this comment has replies. They're just not fetched.
			// An empty array indicates that the comment has not replies.
			if (replies == null) {
				// Display a button that fetches comment replies and adding
				// those to the replies store
				IndentedItem(
					depth = depth + 1,
					indentPadding = 16.dp,
				) {
					TextButton(onClick = {
						replyLoader(comment.id)
					}) {
						Text(text = "Load replies")
					}
				}
			}
		}
	}

	replies?.let { page ->
		if (page.isNotEmpty()) {
			commentNodes(
				comments = page,
				depth = depth + 1,
				replyLoader = replyLoader,
				repliesStore = repliesStore,
				onReplyRecipientChange = onReplyRecipientChange
			)
		}
	}
}

private fun LazyListScope.commentNodes(
	comments: List<Comment>?,
	repliesStore: Map<String, List<CommentReplyPage>>,
	depth: Int = 0,
	replyLoader: (String) -> Unit,
	onReplyRecipientChange: (Comment) -> Unit
) {
	comments?.forEach { comment ->
		commentNode(
			comment = comment,
			repliesStore = repliesStore,
			depth = depth,
			replyLoader = replyLoader,
			onReplyRecipientChange = onReplyRecipientChange
		)
	}
}