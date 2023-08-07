package ca.sxxxi.titter.data.network.models

import ca.sxxxi.titter.data.network.models.responses.PagedResponse
import java.util.Date
import java.util.UUID

data class CommentNM(
	val id: String,
	val content: String,
	val author: UserNM,
	val dateCreated: Long,
	val replies: List<CommentNM>?
)
