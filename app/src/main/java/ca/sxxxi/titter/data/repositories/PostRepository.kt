package ca.sxxxi.titter.data.repositories

import android.util.Log
import ca.sxxxi.titter.data.local.dao.PostsDao
import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.network.CommentsNetworkDataSource
import ca.sxxxi.titter.data.network.PostNetworkDataSource
import ca.sxxxi.titter.data.network.models.PostNM
import ca.sxxxi.titter.data.network.models.forms.PostCreateForm
import ca.sxxxi.titter.data.network.models.responses.PagedResponse
import ca.sxxxi.titter.data.prefs.UserPreferences
import ca.sxxxi.titter.data.utils.contracts.PostMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.await
import java.io.IOException
import javax.inject.Inject

class PostRepository @Inject constructor(
	private val commentsNetworkDataSource: CommentsNetworkDataSource,
	private val postsDao: PostsDao,
	private val dispatcher: CoroutineDispatcher,
	private val postNetSource: PostNetworkDataSource,
	private val authRepo: AuthenticationRepository,
	private val postMapper: PostMapper,
	private val userPrefs: UserPreferences
) {
	suspend fun getPostById(postId: String): Post? = withContext(Dispatchers.IO) {
		val tag = "getPostById"
		try {
			return@withContext postNetSource.getPostById(postId).await().let { nm ->
				postMapper.networkToEntity(nm).let { entity ->
					postMapper.entityToDomain(entity)
				}
			}
		} catch (e: IOException) {
			Log.e(tag, "IOException :: ${e.message}")
			return@withContext null
		} catch (e: HttpException) {
			Log.e(tag, "HttpException :: ${e.message}")
			return@withContext null
		}
	}

	suspend fun getPostPage(page: Int): Result<PagedResponse<List<Post>>> {
		return try {
			if (userPrefs.latestRefreshDate.first() == 0L) {
				userPrefs.setLatestRefreshDate()
			}

			val latestRefresh = userPrefs.latestRefreshDate.first()

			val rawRes = postNetSource.getUserFeed(
				token = authRepo.activeUser.first().key,
				from = latestRefresh,
				pageSize = PAGE_SIZE,
				page = page
			).await()

			val posts = rawRes.content.map { networkModel ->
				postMapper.networkToEntity(networkModel)
			}.map { dbPost ->
//				postsDao.savePost(dbPost)
				postMapper.entityToDomain(dbPost)
			}

			Result.success(
				PagedResponse(
					content = posts,
					first = rawRes.first,
					last = rawRes.last,
					size = rawRes.size,
					totalPages = rawRes.totalPages,
					numberOfElements = rawRes.numberOfElements,
					totalElements = rawRes.totalElements,
					empty = rawRes.empty,
					pageable = rawRes.pageable
				)
			)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	fun invalidatePostCache() = postsDao.deleteAllPosts()

	suspend fun sendPostToRemote(post: PostCreateForm): Result<PostNM> =
		withContext(Dispatchers.IO) {
			return@withContext try {
				val token = authRepo.activeUser.first().key

				Result.success(postNetSource.createPost(token, post).await())
			} catch (e: Exception) {
				Log.e("PostRepository", "$e")
				Result.failure(e)
			}
		}

	companion object {
		const val PAGE_SIZE = 20
	}
}