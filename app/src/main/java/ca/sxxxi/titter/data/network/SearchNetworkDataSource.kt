package ca.sxxxi.titter.data.network

import ca.sxxxi.titter.data.network.models.responses.PagedResponse
import ca.sxxxi.titter.data.network.models.responses.UserSearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchNetworkDataSource {
	@GET("/search/user/{searchTerm}")
	fun searchUser(
		@Header("Authorization") jwt: String,
		@Path("searchTerm") searchTerm: String,
		@Query("page") page: Int,
		@Query("pageSize") pageSize: Int
	): Call<PagedResponse<List<UserSearchResult>>>
}