package ca.sxxxi.titter.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ca.sxxxi.titter.data.models.Comment
import ca.sxxxi.titter.data.models.CommentReplyPage
import ca.sxxxi.titter.data.utils.states.Status
import ca.sxxxi.titter.ui.components.IndentedItem
import ca.sxxxi.titter.ui.components.PagedList
import ca.sxxxi.titter.ui.components.PostCardWithoutButtons
import ca.sxxxi.titter.ui.components.TextInput
import ca.sxxxi.titter.ui.viewmodels.CommentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
	uiState: CommentsViewModel.CommentsUiState,
	onBackPressed: () -> Unit = {},
	repliesLoader: (String, Map<String, List<CommentReplyPage>>, Int) -> Unit,
	onCommentEdit: (String) -> Unit,
	onCommentAdd: () -> Unit
) {
	val comments = uiState.comments.collectAsLazyPagingItems()

	LaunchedEffect(key1 = uiState.commentCreateStatus) {
		if (uiState.commentCreateStatus is Status.Success) {
			comments.refresh()
			onCommentEdit("")
		}
	}

	Column(
		modifier = Modifier.fillMaxSize()
	) {
		TopAppBar(
			title = { Text(text = "Comments") },
			colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = Color.Transparent),
			navigationIcon = {
				IconButton(onClick = onBackPressed) {
					Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back button")
				}
			}
		)
		uiState.post?.let {
			PostCardWithoutButtons(post = it)
		}
		Column(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Divider()
			Text(
				modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
				text = "${comments.itemCount} Comments"
			)
			Divider()
		}

		Column(modifier = Modifier.weight(1f)) {
			CommentsList(
				modifier = Modifier.height(50.dp),
				comments = comments,
				repliesStore = uiState.commentRepliesStore,
				repliesLoader = { commentId ->
					repliesLoader(
						commentId,
						uiState.commentRepliesStore,
						2
					)
				}
			)
		}
		CommentTextBox(
			comment = uiState.commentCreateForm?.content ?: "",
			onCommentEdit = onCommentEdit,
			onCommentAdd = onCommentAdd,
			commentStatus = uiState.commentCreateStatus
		)
	}
}

@Composable
fun CommentTextBox(
	modifier: Modifier = Modifier,
	comment: String,
	onCommentAdd: () -> Unit = {},
	onCommentEdit: (String) -> Unit,
	commentStatus: Status
) {
	val bg = when (commentStatus) {
		is Status.Failure -> MaterialTheme.colorScheme.error
		else -> MaterialTheme.colorScheme.surfaceVariant
	}

	Box(
		modifier = Modifier
			.background(bg)
			.then(modifier)
	) {

		Row(
			modifier = Modifier
				.padding(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			TextInput(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentsList(
	modifier: Modifier = Modifier,
	comments: LazyPagingItems<Comment>,
	repliesStore: Map<String, List<CommentReplyPage>>,
	repliesLoader: (String) -> Unit
) {
	val snackBarHostState = remember { SnackbarHostState() }

	LaunchedEffect(key1 = comments.loadState.refresh) {
		when (comments.loadState.refresh) {
			is LoadState.Error -> {
				snackBarHostState.showSnackbar("Having difficulties fetching post comments.")
			}

			else -> {}
		}
	}

	Scaffold(
		snackbarHost = {
			SnackbarHost(hostState = snackBarHostState)
		}
	) { scaffoldPadding ->
		PagedList(
			modifier = Modifier.padding(scaffoldPadding),
			pagingData = comments
		) {
			commentNodes(
				comments = comments.itemSnapshotList.items,
				repliesStore = repliesStore,
				replyLoader = repliesLoader
			)
		}
	}
}

@Composable
fun CommentCard(comment: Comment) {
	Box(modifier = Modifier.padding(vertical = 8.dp)) {
		Column(
			Modifier
				.padding(start = 20.dp)
				.padding(vertical = 12.dp)
				.width(300.dp)
		) {
			Text(
				text = "${comment.author.fName} ${comment.author.lName}",
				fontWeight = FontWeight.Bold,
				style = MaterialTheme.typography.bodySmall
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(text = comment.content)
		}
	}
}

private fun LazyListScope.commentNode(
	comment: Comment,
	repliesStore: Map<String, List<CommentReplyPage>>,
	depth: Int = 0,
	replyLoader: (commentId: String) -> Unit = {}
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
				CommentCard(comment)
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
						Log.d("BABABOOEY", replies.toString())
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
				repliesStore = repliesStore
			)
		}
	}
}

private fun LazyListScope.commentNodes(
	comments: List<Comment>?,
	repliesStore: Map<String, List<CommentReplyPage>>,
	depth: Int = 0,
	replyLoader: (String) -> Unit
) {
	comments?.forEach { comment ->
		commentNode(
			comment = comment,
			repliesStore = repliesStore,
			depth = depth,
			replyLoader = replyLoader
		)
	}
}