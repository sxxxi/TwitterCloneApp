package ca.sxxxi.titter.data.repositories.post

import android.util.Log
import ca.sxxxi.titter.data.models.CommentReplyPage
import ca.sxxxi.titter.data.network.CommentsNetworkDataSource
import ca.sxxxi.titter.data.network.models.forms.CommentCreateForm
import ca.sxxxi.titter.data.utils.contracts.CommentMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.await
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
	private val commentsNetworkDataSource: CommentsNetworkDataSource,
	private val commentMapper: CommentMapper
) : CommentRepository {
	override suspend fun getCommentRepliesById(
		id: String,
		commentsMap: Map<String, List<CommentReplyPage>>,
		depth: Int,
	): CommentReplyPage? = withContext(Dispatchers.IO) {
		// Get newest page of replies from commentsMap
		val lastPageFetched = commentsMap[id]?.maxByOrNull { it.page }
		val nextPage: Int = (lastPageFetched?.page?.plus(1)) ?: 0
		val pageSize: Int = lastPageFetched?.pageSize ?: 3

		// Make sure this page is not last
		if (lastPageFetched?.lastPage == true) return@withContext null

		val commentNMPage = commentsNetworkDataSource.getCommentReplies(
			commentId = id,
			pageSize = pageSize,
			page = nextPage,
			depth = depth
		).await()

		return@withContext CommentReplyPage(
			content = commentNMPage.content.let {
				it.map { comment -> commentMapper.networkToDomain(comment) }
			},
			page = commentNMPage.pageable.pageNumber,
			pageSize = commentNMPage.pageable.pageSize,
			lastPage = commentNMPage.last
		)
	}

	override suspend fun postComment(
		postId: String,
		jwt: String,
		commentCreateForm: CommentCreateForm
	): Result<Unit> = withContext(Dispatchers.IO) {
		return@withContext try {
			commentsNetworkDataSource.postComment(
				jwt = jwt,
				postId = postId,
				commentCreateForm = commentCreateForm
			).await().let {
				Log.d("FOO", it)
			}
			Result.success(Unit)
		} catch (e: HttpException) {
			Result.failure(e)
		}
	}
}