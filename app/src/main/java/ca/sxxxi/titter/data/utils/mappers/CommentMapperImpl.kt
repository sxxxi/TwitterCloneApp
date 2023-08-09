package ca.sxxxi.titter.data.utils.mappers

import ca.sxxxi.titter.data.models.Comment
import ca.sxxxi.titter.data.network.models.CommentNM
import ca.sxxxi.titter.data.utils.contracts.CommentMapper
import ca.sxxxi.titter.data.utils.contracts.UserMapper

class CommentMapperImpl(private val userMapper: UserMapper) : CommentMapper {
	override fun networkToDomain(net: CommentNM): Comment {
		return Comment(
			id = net.id,
			content = net.content,
			author = userMapper.networkToDomain(net.author),
			dateCreated = net.dateCreated,
			replies = net.replies?.map { commentNM -> networkToDomain(commentNM) }
		)
	}
}