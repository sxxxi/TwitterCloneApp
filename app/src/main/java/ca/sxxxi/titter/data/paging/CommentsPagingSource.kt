package ca.sxxxi.titter.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ca.sxxxi.titter.data.models.Comment
import ca.sxxxi.titter.data.network.CommentsNetworkDataSource
import ca.sxxxi.titter.data.repositories.user.AuthenticationRepository
import ca.sxxxi.titter.data.utils.contracts.CommentMapper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import retrofit2.await
import java.io.IOException

class CommentsPagingSource(
	private val postId: String,
	private val commentsNetSource: CommentsNetworkDataSource,
	private val authenticationRepository: AuthenticationRepository,
	private val commentMapper: CommentMapper
) : PagingSource<Int, Comment>() {
	override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
		return state.anchorPosition?.let { anchorPosition ->
			val anchorPage = state.closestPageToPosition(anchorPosition)
			anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1) ?: null
		}
	}

	override val keyReuseSupported: Boolean
		get() = true

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
		return try {
			val loadKey = params.key ?: 0
			val response = commentsNetSource.getPostComments(
				token = authenticationRepository.activeUser.map { it.key }.first(),
				depth = 1,
				pageSize = 100,
				page = loadKey,
				postId = postId
			).await()
			LoadResult.Page(
				data = response.content.map { commentNM -> commentMapper.networkToDomain(commentNM) },
				prevKey = null,
				nextKey = if (response.last) null else loadKey + 1
			)
		} catch (e: IOException) {
			LoadResult.Error(e)
		} catch (e: HttpException) {
			LoadResult.Error(e)
		}
	}
}