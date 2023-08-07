package ca.sxxxi.titter.data.network.models

import java.util.Date
import java.util.UUID

data class PostNM(
	val id: String,
	val title: String,
	val content: String,
	val dateCreated: Long,
	val author: UserNM,
	val comments: List<CommentNM>? = null
)