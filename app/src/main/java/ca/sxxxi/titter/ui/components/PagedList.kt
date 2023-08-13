package ca.sxxxi.titter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> PagedList(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<T>,
	lazyListState: LazyListState = rememberLazyListState(),
	listEmptyContent: @Composable () -> Unit = { },
	appendLoadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
	content: LazyListScope.() -> Unit
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		LazyColumn(
			modifier = Modifier
				.fillMaxSize(),
			state = lazyListState
		) {content()
			item {
				if (pagingData.itemCount == 0) {
					listEmptyContent()
				} else if (pagingData.loadState.append is LoadState.Loading) {
					appendLoadingContent()
				}
			}
		}
	}
}


@Composable
fun <T : Any> ComposablePagedListContent(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<T>,
	lazyListState: LazyListState = rememberLazyListState(),
	listEmptyContent: @Composable () -> Unit = { },
	appendLoadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
	content: @Composable (T) -> Unit
) {
	PagedList(
		modifier = modifier,
		pagingData = pagingData,
		lazyListState = lazyListState,
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