package ca.sxxxi.titter.ui.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed interface PullState {
	object Pulled : PullState
	object Max : PullState
	object Released : PullState
	object Loading : PullState
	object Neutral : PullState
}

@Composable
fun RefreshableComponent(
	pullDownHandler: () -> Unit,
	refreshIndicator: @Composable (offsetY: Float, pullState: PullState) -> Unit,
	indicatorHeight: Dp,
	isLoading: Boolean = false,
	childScrollState: LazyListState = rememberLazyListState(),
	content: @Composable () -> Unit
) {
	val scope = CoroutineScope(Dispatchers.Default)
	val hideOffset = remember { indicatorHeight.value }
	var scrollVal by remember { mutableFloatStateOf(0f) }
	val refreshIndicatorOffset = remember(hideOffset, scrollVal) { scrollVal - hideOffset }
	val animatedRefreshIndicator by animateFloatAsState(
		targetValue = refreshIndicatorOffset,
		label = "Animated refresh indicator offset"
	)
	val childInteractionSource = childScrollState.interactionSource
	val isDragged by childInteractionSource.collectIsDraggedAsState()
	val inPullPhase = remember(scrollVal, childScrollState) { scrollVal > 0 }
	val maxPullReached = remember(scrollVal, hideOffset) { scrollVal >= (hideOffset * 0.95) }
	var pullState: PullState by remember {
		mutableStateOf(PullState.Neutral)
	}

	val parentScrollableState = rememberScrollableState(consumeScrollDelta = {
		if (scrollVal <= hideOffset && isDragged) {
			scrollVal += it / 7
		}
		if (scrollVal > hideOffset) scrollVal = hideOffset
		it
	})

	// Side effect for adding behavior on each state
	LaunchedEffect(key1 = pullState) {
		Log.d("PullState", "$pullState")
		when (pullState) {
			PullState.Released -> {
				scope.launch {
					pullDownHandler()
				}
				pullState = PullState.Loading
			}

			else -> {}
		}
	}

	// Side effect for managing the states
	LaunchedEffect(isDragged, pullState, isLoading, maxPullReached, inPullPhase) {
		if (isDragged) {
			pullState = if (inPullPhase) {
				if (maxPullReached) {
					PullState.Max
				} else {
					PullState.Pulled
				}
			} else {
				PullState.Neutral
			}
		} else {
			when (pullState) {
				is PullState.Pulled -> {
					pullState = PullState.Neutral
				}

				is PullState.Max -> {
					pullState = PullState.Released
				}

				is PullState.Loading -> {
					pullState = if (!isLoading)
						PullState.Neutral
					else
						PullState.Loading
				}

				else -> {}
			}

			if (pullState != PullState.Loading && pullState != PullState.Released) {
				scrollVal = 0f
			}
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.scrollable(
				orientation = Orientation.Vertical,
				state = parentScrollableState,
			),
	) {
		content()
		if (pullState != PullState.Neutral)
			refreshIndicator(animatedRefreshIndicator, pullState)
	}
}