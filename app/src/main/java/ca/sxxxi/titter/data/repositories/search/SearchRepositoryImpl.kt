package ca.sxxxi.titter.data.repositories.search

import ca.sxxxi.titter.data.models.Page
import ca.sxxxi.titter.data.models.UserSearchItem
import ca.sxxxi.titter.data.network.SearchNetworkDataSource
import ca.sxxxi.titter.data.network.models.responses.UserSearchResult
import ca.sxxxi.titter.data.utils.contracts.PageMapper
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchRepositoryImpl @Inject constructor(
	private val searchService: SearchNetworkDataSource,
	private val pageMapper: PageMapper<List<UserSearchResult>, List<UserSearchItem>>
) : SearchRepository {
	override suspend fun searchUser(
		jwt: String,
		term: String,
		page: Int,
		pageSize: Int,
		context: CoroutineContext
	): Page<List<UserSearchItem>> = withContext(context = context) {
		return@withContext searchService.searchUser(
			jwt = jwt,
			searchTerm = term,
			page = page,
			pageSize = pageSize
		).await().let { searchResults ->
			pageMapper.networkToDomain(searchResults)
		}
	}
}