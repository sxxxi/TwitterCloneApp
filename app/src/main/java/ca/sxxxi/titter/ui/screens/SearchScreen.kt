package ca.sxxxi.titter.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ca.sxxxi.titter.data.models.UserSearchItem
import ca.sxxxi.titter.ui.components.ComposablePagedListContent
import ca.sxxxi.titter.ui.components.NewInput
import ca.sxxxi.titter.ui.components.UserCardWithFollowButton
import ca.sxxxi.titter.ui.viewmodels.SearchViewModel

@Composable
fun SearchScreen(
	uiState: SearchViewModel.SearchUiState,
	onSearchTermEdit: (String) -> Unit,
	onSearch: (String) -> Unit,
	onFollow: (String, MutableState<Boolean>) -> Unit,
	onUnfollow: (String, MutableState<Boolean>) -> Unit
) {
	val searchResults = uiState.userSearchResultsFlow?.collectAsLazyPagingItems()
	val localFocusManager = LocalFocusManager.current

	Column(
		modifier = Modifier
			.fillMaxSize()
			.pointerInput(Unit) {
				detectTapGestures { localFocusManager.clearFocus() }
			},
		verticalArrangement = Arrangement.spacedBy(24.dp)
	) {
		SearchBar(
			searchTerm = uiState.searchTerm,
			onSearchTermEdit = onSearchTermEdit,
			onSearch = onSearch
		)

		searchResults?.let {
			UserSearchResultSet(
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
					.padding(horizontal = 8.dp),
				searchResults = searchResults,
				onFollow = onFollow,
				onUnfollow = onUnfollow
			)
		}
	}
}

@Composable
private fun SearchBar(
	searchTerm: String,
	onSearchTermEdit: (String) -> Unit = {},
	onSearch: (String) -> Unit = {},
	focusManager: FocusManager = LocalFocusManager.current
) {
	Row(
		modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		NewInput(
			value = searchTerm,
			onValueChange = onSearchTermEdit,
			trailingIcon = { Icon(
				modifier = Modifier.padding(start = 16.dp, end = 8.dp),
				imageVector = Icons.Default.Search,
				contentDescription = "Search"
			) },
			keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
			keyboardActions = KeyboardActions(
				onSearch = {
					focusManager.clearFocus()
					onSearch(searchTerm)
				}
			)
		)

//		TextInput(
//			modifier = Modifier.weight(1f),
//			value = searchTerm,
//			onValueChange = onSearchTermEdit,
//			label = "Search users",
//		)
//		IconButton(
//			onClick = { onSearch(searchTerm) }
//		) {
//			Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//		}
	}
}

@Composable
private fun UserSearchResultSet(
	modifier: Modifier = Modifier,
	searchResults: LazyPagingItems<UserSearchItem>,
	onFollow: (String, MutableState<Boolean>) -> Unit,
	onUnfollow: (String, MutableState<Boolean>) -> Unit
) {
	ComposablePagedListContent(
		modifier = modifier,
		pagingData = searchResults,
		listEmptyContent = { Text(text = "No results found") },
	) { result ->
		UserCardWithFollowButton(
			modifier = Modifier.fillMaxWidth(),
			result = result,
			onFollow = onFollow,
			onUnfollow = onUnfollow
		)
		Spacer(modifier = Modifier.height(8.dp))
	}
}