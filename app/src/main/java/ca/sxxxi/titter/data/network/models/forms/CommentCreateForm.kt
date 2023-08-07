package ca.sxxxi.titter.data.network.models.forms

data class CommentCreateForm(
	val recipientId: String? = null,
	val content: String
)