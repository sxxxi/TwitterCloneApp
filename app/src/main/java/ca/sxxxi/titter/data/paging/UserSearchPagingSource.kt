package ca.sxxxi.titter.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ca.sxxxi.titter.data.models.Page
import ca.sxxxi.titter.data.models.UserSearchItem
import kotlinx.coroutines.Deferred

class UserSearchPagingSource(
	private val dataLoader: (loadKey: Int) -> Deferred<Page<List<UserSearchItem>>>
) : PagingSource<Int, UserSearchItem>() {
	override fun getRefreshKey(state: PagingState<Int, UserSearchItem>): Int? {
		return state.anchorPosition?.let { anchorPosition ->
			val anchorPage = state.closestPageToPosition(anchorPosition)
			anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
		}
	}

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserSearchItem> {
		return try {
			val loadKey = params.key ?: 0
			dataLoader(loadKey).await().let { results ->
				LoadResult.Page(
					data = results.content,
					prevKey = null,
					nextKey = if (results.isLast) null else loadKey + 1
				)
			}
		} catch (e: Exception) {
			LoadResult.Error(e)
		}
	}
}