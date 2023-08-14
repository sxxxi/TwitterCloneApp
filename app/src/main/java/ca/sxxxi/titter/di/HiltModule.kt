package ca.sxxxi.titter.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.RemoteMediator
import androidx.room.Room
import ca.sxxxi.titter.activeUser.ActiveUser
import ca.sxxxi.titter.activeUserDataStore
import ca.sxxxi.titter.data.cache.ActiveUserCache
import ca.sxxxi.titter.data.cache.Cachable
import ca.sxxxi.titter.data.local.PostDb
import ca.sxxxi.titter.data.local.dao.PostsDao
import ca.sxxxi.titter.data.local.entities.combine.PostWithUser
import ca.sxxxi.titter.data.network.PostNetworkDataSource
import ca.sxxxi.titter.data.prefs.ConcreteUserPreferences
import ca.sxxxi.titter.data.prefs.UserPreferences
import ca.sxxxi.titter.data.repositories.user.AuthenticationRepository
import ca.sxxxi.titter.data.paging.PostsRemoteMediator
import ca.sxxxi.titter.data.utils.contracts.PostMapper
import ca.sxxxi.titter.proto.Settings
import ca.sxxxi.titter.userPrefsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

	@Provides
	fun activeUserCacheDataStore(@ApplicationContext context: Context): DataStore<ActiveUser> {
		return context.activeUserDataStore
	}

	@Provides
	fun userPreferencesDataStore(@ApplicationContext context: Context): DataStore<Settings> {
		return context.userPrefsDataStore
	}

	@Provides
	fun activeUserCache(ds: DataStore<ActiveUser>): Cachable<ActiveUser> {
		return ActiveUserCache(ds)
	}

	@Provides
	fun userPreferences(ds: DataStore<Settings>): UserPreferences {
		return ConcreteUserPreferences(ds)
	}

	@Provides
	fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

	@Provides
	fun postsDao(@ApplicationContext ctx: Context): PostsDao {
		return PostDb.dao(ctx)
	}

	@Provides
	fun postsDb(@ApplicationContext context: Context): PostDb {
		return Room.databaseBuilder(context = context, PostDb::class.java, name = "posts.db").build()
	}


	@OptIn(ExperimentalPagingApi::class)
	@Provides
	fun postsPager(
		postDb: PostDb,
		postNetSource: PostNetworkDataSource,
		userPreferences: UserPreferences,
		authRepo: AuthenticationRepository,
		postMapper: PostMapper
	): Pager<Int, PostWithUser> {
		return Pager(
			config = PagingConfig(pageSize = 20),
			remoteMediator = PostsRemoteMediator(
				postsDb = postDb,
				postsNetworkService = postNetSource,
				userPreferences = userPreferences,
				authRepo = authRepo,
				postMapper = postMapper
			),
			pagingSourceFactory = { postDb.postDao().getCachedPosts() }
		)

	}

	@OptIn(ExperimentalPagingApi::class)
	@Provides
	fun postsRemoteMediator(
		postDb: PostDb,
		postNetSource: PostNetworkDataSource,
		userPreferences: UserPreferences,
		authRepo: AuthenticationRepository,
		postMapper: PostMapper
	): RemoteMediator<Int, PostWithUser> {
		return PostsRemoteMediator(
			postsDb = postDb,
			postsNetworkService = postNetSource,
			userPreferences = userPreferences,
			authRepo = authRepo,
			postMapper = postMapper
		)
	}

//	@Provides
//	fun userSearchPagingSource(
//		userSearchNetworkDataSource: SearchNetworkDataSource
//	): UserSearchPagingSource {
//		return UserSearchPagingSource(userSearchNetworkDataSource)
//	}
}