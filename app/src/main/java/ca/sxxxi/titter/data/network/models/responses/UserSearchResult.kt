package ca.sxxxi.titter.data.network.models.responses

import ca.sxxxi.titter.data.network.models.UserNM

data class UserSearchResult(
	val userInfo: UserNM,
	val followed: Boolean
)