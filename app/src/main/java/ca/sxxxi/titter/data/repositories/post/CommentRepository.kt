package ca.sxxxi.titter.data.repositories.post

import ca.sxxxi.titter.data.models.CommentReplyPage
import ca.sxxxi.titter.data.network.models.forms.CommentCreateForm

interface CommentRepository {

	suspend fun getCommentRepliesById(
		id: String,
		commentsMap: Map<String, List<CommentReplyPage>>,
		depth: Int = 0,
	): CommentReplyPage?

	suspend fun postComment(
		postId: String,
		jwt: String,
		commentCreateForm: CommentCreateForm
	): Result<Unit>
}
