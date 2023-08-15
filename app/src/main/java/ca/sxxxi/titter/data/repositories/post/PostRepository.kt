package ca.sxxxi.titter.data.repositories.post

import ca.sxxxi.titter.data.models.Post
import ca.sxxxi.titter.data.network.models.PostNM
import ca.sxxxi.titter.data.network.models.forms.PostCreateForm
import ca.sxxxi.titter.data.network.models.responses.PagedResponse

interface PostRepository {
	suspend fun getPostById(postId: String): Post?
	suspend fun getPostPage(page: Int): Result<PagedResponse<List<Post>>>
	fun invalidatePostCache()
	suspend fun sendPostToRemote(post: PostCreateForm): Result<PostNM>
}