package ca.sxxxi.titter.data.repositories.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ca.sxxxi.titter.data.local.PostDb
import ca.sxxxi.titter.data.local.entities.combine.PostWithUser
import ca.sxxxi.titter.data.network.PostNetworkDataSource
import ca.sxxxi.titter.data.prefs.UserPreferences
import ca.sxxxi.titter.data.repositories.AuthenticationRepository
import ca.sxxxi.titter.data.utils.contracts.PostMapper
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import retrofit2.await
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator(
	private val postsDb: PostDb,
	private val postsNetworkService: PostNetworkDataSource,
	private val userPreferences: UserPreferences,
	private val authRepo: AuthenticationRepository,
	private val postMapper: PostMapper,
) : RemoteMediator<Int, PostWithUser>() {
	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, PostWithUser>
	): MediatorResult {
		return try {
			val currentPage = userPreferences.currentPage.first()
			val loadKey = when (loadType) {
				LoadType.REFRESH -> {
					userPreferences.refresh()
					0
				}

				LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
				LoadType.APPEND -> currentPage + 1
			}
			val response = postsNetworkService.getUserFeed(
				token = authRepo.activeUser.first().key,
				from = userPreferences.latestRefreshDate.first(),
				page = loadKey,
				pageSize = state.config.pageSize
			).await()

			if (response.totalPages > currentPage) {
				userPreferences.nextPage()
			}

			postsDb.withTransaction {
				val postsDao = postsDb.postDao()
				if (loadType == LoadType.REFRESH) {
					postsDao.deleteAllPosts()
				}
				postsDao.savePosts(response.content.map { postMapper.networkToEntity(it) })
			}

			MediatorResult.Success(endOfPaginationReached = response.last)
		} catch (e: IOException) {
			MediatorResult.Error(e)
		} catch (e: HttpException) {
			MediatorResult.Error(e)
		}
	}
}