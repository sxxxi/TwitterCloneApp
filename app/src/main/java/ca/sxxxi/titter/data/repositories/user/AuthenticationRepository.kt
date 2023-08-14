package ca.sxxxi.titter.data.repositories.user

import ca.sxxxi.titter.activeUser.ActiveUser
import ca.sxxxi.titter.data.local.entities.UserEntity
import ca.sxxxi.titter.data.network.models.forms.LoginRequest
import ca.sxxxi.titter.data.network.models.forms.SignupRequest
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
	val activeUser: Flow<ActiveUser>

	// Wait, as long as i have the token and the id i can do anything!! why am i storing the profile???
	suspend fun authenticate(loginRequest: LoginRequest): Result<Unit>
	suspend fun signup(req: SignupRequest): Result<Unit>
	suspend fun clear()
	suspend fun getRegisteredUserById(uname: String): Result<UserEntity>
}

