package ca.sxxxi.titter.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import kotlin.math.abs

@Composable
fun <T : Any> RefreshablePagedList(
	modifier: Modifier = Modifier,
	pagingData: LazyPagingItems<T>,
	scrollState: LazyListState = rememberLazyListState(),
	refreshIndicator: @Composable (offset: Float, pullState: PullState) -> Unit = { y, state ->
		DefaultRefreshIndicator(height = defaultRefreshHeight, offset = y, pullState = state)
	},
	indicatorHeight: Dp = defaultRefreshHeight.dp,
	refreshHandler: () -> Unit = {},
	isRefreshing: Boolean = false,
	listEmptyContent: @Composable () -> Unit = { },
	appendLoadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
	content: LazyListScope.() -> Unit,
) {
	RefreshableComponent(
		pullDownHandler = refreshHandler,
		isLoading = isRefreshing,
		refreshIndicator = refreshIndicator,
		indicatorHeight = indicatorHeight,
		childScrollState = scrollState,
	) {
		PagedList(
			modifier = modifier,
			pagingData = pagingData,
			lazyListState = scrollState,
			listEmptyContent = listEmptyContent,
			appendLoadingContent = appendLoadingContent,
			content = content
		)
	}
}

private val defaultRefreshHeight = 100.dp.value

@Composable
fun DefaultRefreshIndicator(
	height: Float,
	offset: Float,
	pullState: PullState
) {
	Column(
		Modifier.fillMaxWidth()
			.offset(y = offset.dp)
		,
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		val isVisible = remember(pullState) { pullState != PullState.Neutral }
		AnimatedVisibility(visible = isVisible) {
			when (pullState) {
				PullState.Pulled, PullState.Max -> {
					CircularProgressIndicator(progress = (abs(offset) - height) / height)
				}
				PullState.Released, PullState.Loading -> {
					CircularProgressIndicator()
				}
				else -> {}
			}
		}
	}
}