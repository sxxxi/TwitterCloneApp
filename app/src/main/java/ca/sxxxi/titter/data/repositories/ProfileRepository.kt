package ca.sxxxi.titter.data.repositories

import ca.sxxxi.titter.data.network.ProfileNetworkDataSource
import retrofit2.HttpException
import retrofit2.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
	private val profileNetworkDataSource: ProfileNetworkDataSource
) {
	suspend fun followUser(jwt: String, userId: String): Result<Unit> {
		return try {
			profileNetworkDataSource.followUser(jwt, userId).await()
			Result.success(Unit)
		} catch (e: HttpException) {
			Result.failure(e)
		}
	}

	suspend fun unfollowUser(jwt: String, userId: String): Result<Unit> {
		return try {
			profileNetworkDataSource.unfollowUser(jwt, userId).await()
			Result.success(Unit)
		} catch (e: HttpException) {
			Result.failure(e)
		}
	}
}