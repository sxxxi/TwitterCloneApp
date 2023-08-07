package ca.sxxxi.titter.di

import ca.sxxxi.titter.data.utils.contracts.CommentMapper
import ca.sxxxi.titter.data.utils.mappers.ConcretePostMapper
import ca.sxxxi.titter.data.utils.mappers.ConcreteUserMapper
import ca.sxxxi.titter.data.utils.contracts.PostMapper
import ca.sxxxi.titter.data.utils.contracts.UserMapper
import ca.sxxxi.titter.data.utils.mappers.ConcreteCommentMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
	@Provides
	fun userMapper(): UserMapper {
		return ConcreteUserMapper()
	}

	@Provides
	fun postMapper(): PostMapper {
		return ConcretePostMapper(userMapper())
	}

	@Provides
	fun commentMapper(userMapper: UserMapper): CommentMapper {
		return ConcreteCommentMapper(userMapper)
	}
}