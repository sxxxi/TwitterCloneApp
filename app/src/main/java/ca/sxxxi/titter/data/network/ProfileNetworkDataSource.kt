package ca.sxxxi.titter.data.network

import ca.sxxxi.titter.data.network.models.UserNM
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface ProfileNetworkDataSource {
	@GET("/profile/{uid}")
	fun getUserProfile(@Path("uid") uid: String): Call<UserNM>

	@POST("/follow/{followed}")
	fun followUser(
		@Header("Authorization") jwt: String,
		@Path("followed") followedId: String
	): Call<Unit>

	@DELETE("/unfollow/{followed}")
	fun unfollowUser(
		@Header("Authorization") jwt: String,
		@Path("followed") followedId: String
	): Call<Unit>
}