package ca.sxxxi.titter.data.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import ca.sxxxi.titter.activeUser.ActiveUser
import ca.sxxxi.titter.data.cache.ActiveUserCache
import ca.sxxxi.titter.data.cache.Cachable
import ca.sxxxi.titter.data.local.dao.PostsDao
import ca.sxxxi.titter.data.local.entities.UserEntity
import ca.sxxxi.titter.data.network.AuthenticationNetworkDataSource
import ca.sxxxi.titter.data.network.ProfileNetworkDataSource
import ca.sxxxi.titter.data.network.models.forms.LoginRequest
import ca.sxxxi.titter.data.network.models.forms.SignupRequest
import ca.sxxxi.titter.data.utils.exceptions.AuthenticationException.LoginFailed
import ca.sxxxi.titter.data.utils.exceptions.NetworkError.ConnectionFailed
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.await
import java.net.ConnectException
import javax.inject.Inject
import kotlin.math.log

class AuthenticationRepository @Inject constructor(
	@ApplicationContext private val context: Context,
	activeUserDataStore: DataStore<ActiveUser>,
	private val postsDao: PostsDao,
	private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
	private val authNetSource: AuthenticationNetworkDataSource,
	private val profileNetSource: ProfileNetworkDataSource
) {
	private val scope = CoroutineScope(coroutineDispatcher)
	private val cache: Cachable<ActiveUser> = ActiveUserCache(activeUserDataStore)
	val activeUser: Flow<ActiveUser> = cache.value

	// Wait, as long as i have the token and the id i can do anything!! why am i storing the profile???
	suspend fun authenticate(loginRequest: LoginRequest): Result<Unit> = withContext(coroutineDispatcher) {
		return@withContext try {
			val token = getToken(loginRequest)
			val profile = withContext(Dispatchers.IO) {
				profileNetSource.getUserProfile(loginRequest.username).await()
			}

			runBlocking {
				updateActiveUser {
					it.toBuilder()
						.setKey(token)
						.setId(profile.id)
						.setFirstName(profile.firstName)
						.setLastName(profile.lastName)
						.build()
				}
			}
			Result.success(Unit)
		} catch(e: Exception) {
			Result.failure(
				when (e) {
					is ConnectException -> ConnectionFailed("Cannot reach server")
					else -> e
				}
			)
		}
	}

	private suspend fun getToken(loginRequest: LoginRequest): String = withContext(Dispatchers.IO) {
		try {
			return@withContext authNetSource.login(loginRequest).await().token
		} catch (e: Exception) {
			throw when (e) {
				is HttpException -> LoginFailed("Username or password incorrect.")
				else -> e
			}
		}
	}

	suspend fun signup(req: SignupRequest): Result<Unit> = withContext(coroutineDispatcher) {
		return@withContext try {
			authNetSource.signup(req).await()
			Result.success(Unit)
		} catch (e: Exception) {
			Log.e(TAG, "Something went wrong during signup. Prolly in the dao\t$e")
			Result.failure(e)
		}
	}

	suspend fun clear() = withContext(coroutineDispatcher) { cache.clear() }

	private suspend fun updateActiveUser(transform: (ActiveUser) -> ActiveUser) = withContext(coroutineDispatcher) {
		cache.update { activeUser -> transform(activeUser) }
	}

	private fun storeAndReturnUserPfpPath(pfp: ByteArray, username: String): String {
		return context.openFileOutput(username, Context.MODE_PRIVATE).use {
			it.write(pfp)
			"${context.filesDir}/$username"
		}
	}

	suspend fun getRegisteredUserById(uname: String): Result<UserEntity> = withContext(coroutineDispatcher) {
		return@withContext try {
			Result.success(postsDao.getRegisteredUser(uname))
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	companion object {
		private const val TAG = "AuthenticationRepository"
	}
}