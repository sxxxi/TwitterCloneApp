package ca.sxxxi.titter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import ca.sxxxi.titter.data.utils.states.Status

@Composable
fun <T: Any> PagedList(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<T>,
	refreshLoadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
	refreshErrorContent: @Composable () -> Unit = { },
	listEmptyContent: @Composable () -> Unit = { },
	appendLoadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
	content: LazyListScope.() -> Unit
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		when (pagingData.loadState.refresh) {
			is LoadState.NotLoading -> {
				LazyColumn(Modifier.weight(1f)) {
					content()
					item {
						if (pagingData.itemCount == 0) {
							listEmptyContent()
						} else if (pagingData.loadState.append is LoadState.Loading) {
							appendLoadingContent()
						}
					}
				}
			}
			is LoadState.Loading -> refreshLoadingContent()
			is LoadState.Error -> refreshErrorContent()
		}

	}
}


@Composable
fun <T: Any> ComposablePagedListContent(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<T>,
	refreshLoadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
	refreshErrorContent: @Composable () -> Unit = { },
	listEmptyContent: @Composable () -> Unit = { },
	appendLoadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
	content: @Composable (T) -> Unit
) {
	PagedList(
		modifier = modifier,
		pagingData = pagingData,
		refreshLoadingContent = refreshLoadingContent,
		refreshErrorContent = refreshErrorContent,
		listEmptyContent = listEmptyContent,
		appendLoadingContent = appendLoadingContent,
		content = {
			items(count = pagingData.itemCount, key = { it }) {
				pagingData[it]?.let { item ->
					content(item)
				}
			}
		}
	)
}