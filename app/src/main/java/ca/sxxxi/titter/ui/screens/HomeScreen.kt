package ca.sxxxi.titter.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.*
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.models.User
import ca.sxxxi.titter.ui.components.PostCard
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
) {
	val snackBarHostState = remember { SnackbarHostState() }
	val pagingData = uiState.postPageDataFlow.collectAsLazyPagingItems()

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
		if (uiState.activeUser == null) { onNavigateToAuthentication() }
	}
	
	Scaffold(
		snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
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
			TopBar(
				activeUser = uiState.activeUser,
				onLogout = onLogout,
				onRefresh = { pagingData.refresh() }
			)
			
			PostList(
				modifier = Modifier.fillMaxSize(),
				pagingData = pagingData,
				onCommentsClick = onNavigateToComments
			)
		}
	}
}

@Composable
private fun PostList(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<Post>,
	onCommentsClick: (String) -> Unit = {}
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {

		if (pagingData.loadState.refresh is LoadState.Loading) {
			CircularProgressIndicator()
		} else {
			LazyColumn {
				items(count = pagingData.itemCount) { index ->
					pagingData[index]?.let { post ->
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
				item {
					if (pagingData.loadState.append is LoadState.Loading) {
						CircularProgressIndicator()
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
	activeUser: User?,
	onLogout: () -> Unit = {},
	onRefresh: () -> Unit = {}
) {
	TopAppBar(
		title = { Text(text = "Home") },
		colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = Color.Transparent),
		actions = {
			activeUser?.let {
				Row {
					TextButton(onClick = onLogout) {
						Text(text = "Logout", color = MaterialTheme.colorScheme.error)
					}
					TextButton(onClick = onRefresh) {
						Text(text = "Refresh")
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
				postPageDataFlow = flow {  }
			))
		}
	}
}