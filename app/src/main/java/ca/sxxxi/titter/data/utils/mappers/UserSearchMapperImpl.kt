package ca.sxxxi.titter.data.utils.mappers

import ca.sxxxi.titter.data.models.UserSearchItem
import ca.sxxxi.titter.data.network.models.responses.UserSearchResult
import ca.sxxxi.titter.data.utils.contracts.UserMapper
import ca.sxxxi.titter.data.utils.contracts.UserSearchMapper
import javax.inject.Inject

class UserSearchMapperImpl @Inject constructor(
	private val userMapper: UserMapper
) : UserSearchMapper {
	override fun networkToDomain(net: List<UserSearchResult>): List<UserSearchItem> {
		return net.map { res ->
			UserSearchItem(
				user = userMapper.networkToDomain(res.userInfo),
				followed = res.followed
			)
		}
	}
}