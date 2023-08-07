package ca.sxxxi.titter.data.network

import ca.sxxxi.titter.data.network.models.CommentNM
import ca.sxxxi.titter.data.network.models.forms.CommentCreateForm
import ca.sxxxi.titter.data.network.models.responses.PagedResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface CommentsNetworkDataSource {
	@GET("/posts/{postId}/comments")
	fun getPostComments(
		@Header("Authorization") token: String,
		@Path("postId") postId: String,
		@Query("depth") depth: Int,
		@Query("page") page: Int = 0,
		@Query("pageSize") pageSize: Int = 0
	): Call<PagedResponse<List<CommentNM>>>

	@GET("/posts/comments/{commentId}/replies")
	fun getCommentReplies(
		@Path("commentId") commentId: String,
		@Query("depth") depth: Int,
		@Query("page") page: Int = 0,
		@Query("pageSize") pageSize: Int = 0
	): Call<PagedResponse<List<CommentNM>>>

	@POST("/posts/{postId}/comment")
	fun postComment(
		@Path("postId") postId: String,
		@Header("Authorization") jwt: String,
		@Body commentCreateForm: CommentCreateForm
	): Call<String>
}