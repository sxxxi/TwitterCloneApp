package ca.sxxxi.titter.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.*
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.models.User
import ca.sxxxi.titter.ui.components.ComposablePagedListContent
import ca.sxxxi.titter.ui.components.PostCard
import ca.sxxxi.titter.ui.components.PullState
import ca.sxxxi.titter.ui.components.RefreshableComponent
import ca.sxxxi.titter.ui.theme.TitterTheme
import ca.sxxxi.titter.ui.viewmodels.HomeViewModel.HomeUiState
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	modifier: Modifier = Modifier,
	uiState: HomeUiState = HomeUiState(postPageDataFlow = flow {}),
	onPostCreateClicked: () -> Unit = {},
	onNavigateToAuthentication: () -> Unit = {},
	onLogout: () -> Unit = {},
	onNavigateToComments: (String) -> Unit = {},
	onNavigateToSearch: () -> Unit = {}
) {
	val snackBarHostState = remember { SnackbarHostState() }
	val pagingData = uiState.postPageDataFlow.collectAsLazyPagingItems()
	val isRefreshing = remember(pagingData.loadState.refresh) {
		pagingData.loadState.refresh !is LoadState.NotLoading
	}
	val childListState = rememberLazyListState()

	LaunchedEffect(key1 = pagingData.loadState) {
		// Show error on refresh error
		if (
			pagingData.loadState.refresh is LoadState.Error ||
			pagingData.loadState.append is LoadState.Error
		) {
			snackBarHostState.showSnackbar(message = "Error fetching remote resources.")
		}
	}

	LaunchedEffect(key1 = uiState.activeUser) {
		Log.d("HomeScreen", "${uiState.activeUser == null}")
		if (uiState.activeUser == null) {
			onNavigateToAuthentication()
		}
	}

	Scaffold(
		snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
		topBar = {
			TopBar(
				activeUser = uiState.activeUser,
				onLogout = onLogout,
				onSearch = onNavigateToSearch,
				onRefresh = { pagingData.refresh() }
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onPostCreateClicked) {
				Icon(imageVector = Icons.Default.Add, contentDescription = null)
			}
		}
	) {
		Column(
			Modifier
				.then(modifier)
				.padding(it)
		) {
			PostList(
				modifier = Modifier.fillMaxSize(),
				pagingData = pagingData,
				lazyListState = childListState,
				onCommentsClick = onNavigateToComments,
				isRefreshing = isRefreshing,
				childListState = childListState
			)
		}
	}
}

@Composable
private fun PostList(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<Post>,
	lazyListState: LazyListState = rememberLazyListState(),
	onCommentsClick: (String) -> Unit = {},
	isRefreshing: Boolean,
	childListState: LazyListState
) {
	RefreshableComponent(
		childScrollState = childListState,
		refreshIndicator = { offsetY, pullState ->
			Column(
				modifier = Modifier
					.height(100.dp)
					.offset(y = offsetY.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					modifier = Modifier
						.background(MaterialTheme.colorScheme.primaryContainer)
						.weight(1f)
						.fillMaxWidth()
					,
					text = when (pullState) {
						is PullState.Neutral -> ""
						is PullState.Max -> "Release to refresh"
						is PullState.Loading -> "Refreshing"
						else -> "Release to cancel"
					},

					style = MaterialTheme.typography.headlineMedium + TextStyle(
						color = MaterialTheme.colorScheme.primary,
						fontWeight = FontWeight.Bold,
						textAlign = TextAlign.Center,
					)
				)
			}
		},
		indicatorHeight = 100.dp,
		pullDownHandler =  {
			pagingData.refresh()
		},
		isLoading = isRefreshing
	) {
		ComposablePagedListContent(
			modifier = modifier,
			pagingData = pagingData,
			lazyListState = lazyListState,
			refreshErrorContent = { Text(text = "Unable to reach server") }
		) { post ->
			PostCard(
				post = post,
				onCommentsClicked = onCommentsClick
			)

			Divider(
				modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
				thickness = 1.dp,
				color = MaterialTheme.colorScheme.outlineVariant
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
	activeUser: User?,
	onLogout: () -> Unit = {},
	onRefresh: () -> Unit = {},
	onSearch: () -> Unit = {}
) {
	TopAppBar(
		title = { Text(text = "Home", style = MaterialTheme.typography.titleLarge) },
		colors = TopAppBarDefaults
			.mediumTopAppBarColors(scrolledContainerColor = Color.Transparent),
		actions = {
			activeUser?.let {
				Row {
					IconButton(onClick = onLogout) {
						Icon(
							imageVector = Icons.Default.ExitToApp,
							contentDescription = "",
							tint = MaterialTheme.colorScheme.error
						)
					}
					IconButton(onClick = onRefresh) {
						Icon(imageVector = Icons.Default.Refresh, contentDescription = "")
					}
					IconButton(onClick = onSearch) {
						Icon(imageVector = Icons.Default.Search, contentDescription = "")
					}
				}
			}
		})
}

@Preview
@Composable
fun PreviewHomeScreen() {
	TitterTheme {
		Surface(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			HomeScreen(uiState = HomeUiState(
				postPageDataFlow = flow {
					emit(PagingData.from(
						listOf(
							Post(
								author = User(
									id = "seiji",
									fName = "Seiji",
									lName = "Akakabe"
								),
								content = "Hi there!"
							)
						)
					))
				}
			))
		}
	}
}