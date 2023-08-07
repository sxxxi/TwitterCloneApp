package ca.sxxxi.titter.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CommentEntity(
	@PrimaryKey
	@ColumnInfo("commentId")
	val id: String,
	val content: String = "",
	val author: String
)