package ca.sxxxi.titter.data.repositories.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.repositories.PostRepository

// Paging without caching in the local database
class PostsPagingSource(
	private val backend: PostRepository
) : PagingSource<Int, Post>() {
	override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
		return state.anchorPosition?.let { anchorPosition ->
			val anchorPage = state.closestPageToPosition(anchorPosition)
			anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
		}
	}
	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
		try {
			// Start refresh at page 1 if undefined.
			val nextPageNumber = params.key ?: 1
			val response = backend.getPostPage(nextPageNumber).getOrElse {
				throw it
			}
			return LoadResult.Page(
				data = response.content,
				nextKey = if (response.last) null else nextPageNumber + 1,
				prevKey = nextPageNumber
			)
		} catch (e: Exception) {
			return LoadResult.Error(e)
			// Handle errors in this block and return LoadResult.Error for
			// expected errors (such as a network failure).
		}
	}

	override val keyReuseSupported: Boolean
		get() = true
}