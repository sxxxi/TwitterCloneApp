package ca.sxxxi.titter.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
	@PrimaryKey
	@ColumnInfo("userId")
	val id: String,
	val pfp: String? = null,
	val fName: String = "",
	val lName: String = "",
)
