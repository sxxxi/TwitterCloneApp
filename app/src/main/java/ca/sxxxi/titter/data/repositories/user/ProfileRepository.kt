package ca.sxxxi.titter.data.repositories.user

interface ProfileRepository {
	suspend fun followUser(jwt: String, userId: String): Result<Unit>
	suspend fun unfollowUser(jwt: String, userId: String): Result<Unit>
}

