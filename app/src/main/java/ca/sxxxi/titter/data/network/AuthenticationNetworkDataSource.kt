package ca.sxxxi.titter.data.network

import ca.sxxxi.titter.data.network.models.forms.AuthenticationResponse
import ca.sxxxi.titter.data.network.models.forms.LoginRequest
import ca.sxxxi.titter.data.network.models.forms.SignupRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationNetworkDataSource {
	@POST("/auth/signup")
	fun signup(@Body request: SignupRequest): Call<AuthenticationResponse>

	@POST("/auth/login")
	fun login(@Body request: LoginRequest): Call<AuthenticationResponse>
}