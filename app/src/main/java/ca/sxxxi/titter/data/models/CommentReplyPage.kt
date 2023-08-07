package ca.sxxxi.titter.data.models

data class CommentReplyPage(
	val content: List<Comment>,
	val page: Int,
	val pageSize: Int,
	val lastPage: Boolean,
)