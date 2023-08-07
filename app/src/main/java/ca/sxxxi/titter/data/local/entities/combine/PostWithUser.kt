package ca.sxxxi.titter.data.local.entities.combine

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import ca.sxxxi.titter.data.local.entities.PostEntity
import ca.sxxxi.titter.data.local.entities.UserEntity

@Entity
data class PostWithUser(
	@Embedded val post: PostEntity,
	@Relation(
		parentColumn = "author",
		entityColumn = "userId",
	)
	val user: UserEntity

//	@Embedded val user: UserEntity,
//	@Relation(
//		parentColumn = "postId",
//		entityColumn = "author",
//	)
//	val post: PostEntity,
)