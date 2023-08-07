package ca.sxxxi.titter.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class PostEntity(
	@PrimaryKey
	@ColumnInfo("postId")
	val id: String,
	val author: String = "",
	val content: String = "",
	val dateTimeCreated: Long = Date().toInstant().epochSecond
)