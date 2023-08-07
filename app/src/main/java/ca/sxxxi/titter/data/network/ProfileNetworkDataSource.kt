package ca.sxxxi.titter.data.network

import ca.sxxxi.titter.data.network.models.UserNM
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface ProfileNetworkDataSource {
	@GET("/profile/{uid}")
	fun getUserProfile(@Path("uid") uid: String): Call<UserNM>
}