package ca.sxxxi.titter.data.models

import java.util.Date
import java.util.UUID

data class Post(
	val id: UUID? = null,
	val author: User = User(),
	val content: String = "",
	val dateTimeCreated: Date = Date(),
	val likes: Int = 0,
	val comments: Int = 0,
	val share: Int = 0
)