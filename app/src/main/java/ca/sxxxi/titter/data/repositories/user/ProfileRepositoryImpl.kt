package ca.sxxxi.titter.data.repositories.user

import ca.sxxxi.titter.data.network.ProfileNetworkDataSource
import retrofit2.HttpException
import retrofit2.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
	private val profileNetworkDataSource: ProfileNetworkDataSource
) : ProfileRepository {
	override suspend fun followUser(jwt: String, userId: String): Result<Unit> {
		return try {
			profileNetworkDataSource.followUser(jwt, userId).await()
			Result.success(Unit)
		} catch (e: HttpException) {
			Result.failure(e)
		}
	}

	override suspend fun unfollowUser(jwt: String, userId: String): Result<Unit> {
		return try {
			profileNetworkDataSource.unfollowUser(jwt, userId).await()
			Result.success(Unit)
		} catch (e: HttpException) {
			Result.failure(e)
		}
	}
}