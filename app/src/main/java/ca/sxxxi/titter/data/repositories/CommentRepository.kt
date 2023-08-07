package ca.sxxxi.titter.data.repositories

import ca.sxxxi.titter.data.models.Comment
import ca.sxxxi.titter.data.models.CommentReplyPage
import ca.sxxxi.titter.data.network.CommentsNetworkDataSource
import ca.sxxxi.titter.data.network.models.responses.PagedResponse
import ca.sxxxi.titter.data.utils.contracts.CommentMapper
import retrofit2.await
import javax.inject.Inject

class CommentRepository @Inject constructor(
	private val commentsNetworkDataSource: CommentsNetworkDataSource,
	private val commentMapper: CommentMapper
) {
	suspend fun getCommentRepliesById(
		id: String,
		commentsMap: Map<String, List<CommentReplyPage>>,
		depth: Int = 0,
	): CommentReplyPage? {
		// Get newest page of replies from commentsMap
		val lastPageFetched = commentsMap[id]?.maxByOrNull { it.page }
		val nextPage: Int = (lastPageFetched?.page?.plus(1)) ?: 0
		val pageSize: Int = lastPageFetched?.pageSize ?: 3

		// Make sure this page is not last
		if (lastPageFetched?.lastPage == true) return null

		val commentNMPage = commentsNetworkDataSource.getCommentReplies(
			commentId = id,
			pageSize = pageSize,
			page = nextPage,
			depth = depth
		).await()

		return CommentReplyPage(
			content = commentNMPage.content.let {
				it.map { comment -> commentMapper.networkToDomain(comment) }
			},
			page = commentNMPage.pageable.pageNumber,
			pageSize = commentNMPage.pageable.pageSize,
			lastPage = commentNMPage.last
		)
	}
}