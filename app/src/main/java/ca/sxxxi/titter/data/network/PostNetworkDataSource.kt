package ca.sxxxi.titter.data.network

import ca.sxxxi.titter.data.network.models.PostNM
import ca.sxxxi.titter.data.network.models.forms.PostCreateForm
import ca.sxxxi.titter.data.network.models.responses.PagedResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface PostNetworkDataSource {
	@GET("/feed")
	fun getUserFeed(
		@Header("Authorization") token: String,
		@Query("from") from: Long,
		@Query("pageSize") pageSize: Int,
		@Query("page") page: Int
	): Call<PagedResponse<List<PostNM>>>

	@GET("/posts/pid/{postId}")
	fun getPostById(@Path("postId") postId: String): Call<PostNM>

	@POST("/posts/create")
	fun createPost(@Header("Authorization") token: String, @Body createForm: PostCreateForm): Call<PostNM>
}