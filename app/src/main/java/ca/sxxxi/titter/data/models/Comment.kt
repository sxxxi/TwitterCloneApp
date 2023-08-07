package ca.sxxxi.titter.data.models

import ca.sxxxi.titter.data.network.models.responses.PagedResponse

data class Comment(
	val id: String,
	val content: String,
	val author: User,
	val dateCreated: Long,
	val replies: List<Comment>?
)
