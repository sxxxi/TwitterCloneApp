package ca.sxxxi.titter.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ca.sxxxi.titter.data.local.entities.PostEntity
import ca.sxxxi.titter.data.local.entities.UserEntity
import ca.sxxxi.titter.data.local.entities.combine.PostWithUser
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PostsDao {
	// Query for signing up
	@Insert(onConflict = OnConflictStrategy.ABORT)
	abstract fun signup(newUser: UserEntity)

	@Query("SELECT * FROM UserEntity WHERE userId = :id")
	abstract fun getRegisteredUser(id: String): UserEntity

	// Home page
	@Transaction
	@Query("SELECT * FROM PostEntity")
	abstract fun getPostsWithUsers(): Flow<List<PostWithUser>>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	abstract fun createUser(user: UserEntity)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	abstract fun createPost(post: PostEntity)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	abstract fun createPosts(post: List<PostEntity>)

	@Query("DELETE FROM PostEntity")
	abstract fun deleteAllPosts()

	@Query("SELECT MAX(dateTimeCreated) FROM PostEntity")
	abstract fun getLatestPostDate(): Flow<Long?>

	@Query("SELECT MIN(dateTimeCreated) FROM PostEntity")
	abstract fun getOldestPostDate(): Flow<Long?>

	@Transaction
	open fun savePost(post: PostWithUser) {
		createUser(post.user)
		createPost(post.post)
	}

	@Transaction
	open fun savePosts(post: List<PostWithUser>) {
		post.forEach { post ->
			createUser(post.user)
			createPost(post.post)
		}
	}

	@Query("SELECT * FROM PostEntity ")
	abstract fun getCachedPosts(): PagingSource<Int, PostWithUser>

	@Query("SELECT * FROM PostEntity ORDER BY dateTimeCreated DESC LIMIT 20 OFFSET (:page * 20)")
	abstract fun getCachedPosts(page: Int): PagingSource<Int, PostWithUser>
}