package ca.sxxxi.titter.di

import ca.sxxxi.titter.data.repositories.post.CommentRepository
import ca.sxxxi.titter.data.repositories.post.CommentRepositoryImpl
import ca.sxxxi.titter.data.repositories.post.PostRepository
import ca.sxxxi.titter.data.repositories.post.PostRepositoryImpl
import ca.sxxxi.titter.data.repositories.search.SearchRepository
import ca.sxxxi.titter.data.repositories.search.SearchRepositoryImpl
import ca.sxxxi.titter.data.repositories.user.AuthenticationRepository
import ca.sxxxi.titter.data.repositories.user.AuthenticationRepositoryImpl
import ca.sxxxi.titter.data.repositories.user.ProfileRepository
import ca.sxxxi.titter.data.repositories.user.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
	@Provides
	fun postRepository(impl: PostRepositoryImpl) : PostRepository {
		return impl
	}

	@Provides
	fun commentRepository(impl: CommentRepositoryImpl) : CommentRepository {
		return impl
	}

	@Provides
	fun searchRepository(impl: SearchRepositoryImpl) : SearchRepository = impl

	@Provides
	fun authRepository(impl: AuthenticationRepositoryImpl) : AuthenticationRepository = impl

	@Provides
	fun profileRepostiory(impl: ProfileRepositoryImpl): ProfileRepository = impl
}