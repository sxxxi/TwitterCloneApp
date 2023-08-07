package ca.sxxxi.titter.data.utils.mappers

import ca.sxxxi.titter.data.local.entities.PostEntity
import ca.sxxxi.titter.data.local.entities.combine.PostWithUser
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.network.models.PostNM
import ca.sxxxi.titter.data.utils.contracts.PostMapper
import ca.sxxxi.titter.data.utils.contracts.UserMapper
import java.sql.Date
import java.time.Instant
import java.util.UUID

class ConcretePostMapper(private val userMapper: UserMapper) : PostMapper {
	override fun networkToEntity(source: PostNM): PostWithUser {
		return PostWithUser(
			post = PostEntity(
				id = source.id,
				author = source.author.id,
				content = source.content,
				dateTimeCreated = source.dateCreated
			),
			user = userMapper.networkToEntity(source = source.author)
		)
	}

	override fun entityToDomain(source: PostWithUser): Post {
		val post = source.post
		return Post(
			id = UUID.fromString(post.id),
			author = userMapper.entityToDomain(source.user),
			content = post.content,
			dateTimeCreated = Date.from(Instant.ofEpochSecond(post.dateTimeCreated))
		)
	}
}