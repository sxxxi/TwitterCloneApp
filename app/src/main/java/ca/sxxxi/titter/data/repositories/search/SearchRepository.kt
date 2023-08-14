package ca.sxxxi.titter.data.repositories.search

import ca.sxxxi.titter.data.models.Page
import ca.sxxxi.titter.data.models.UserSearchItem
import kotlin.coroutines.CoroutineContext

interface SearchRepository {
	suspend fun searchUser(
		jwt: String,
		term: String,
		page: Int,
		pageSize: Int,
		context: CoroutineContext
	): Page<List<UserSearchItem>>
}

