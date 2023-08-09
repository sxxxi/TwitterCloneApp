package ca.sxxxi.titter.di

import ca.sxxxi.titter.data.models.UserSearchItem
import ca.sxxxi.titter.data.network.models.responses.UserSearchResult
import ca.sxxxi.titter.data.utils.contracts.CommentMapper
import ca.sxxxi.titter.data.utils.contracts.NDMapper
import ca.sxxxi.titter.data.utils.contracts.PageMapper
import ca.sxxxi.titter.data.utils.mappers.PostMapperImpl
import ca.sxxxi.titter.data.utils.mappers.UserMapperImpl
import ca.sxxxi.titter.data.utils.contracts.PostMapper
import ca.sxxxi.titter.data.utils.contracts.UserMapper
import ca.sxxxi.titter.data.utils.contracts.UserSearchMapper
import ca.sxxxi.titter.data.utils.mappers.CommentMapperImpl
import ca.sxxxi.titter.data.utils.mappers.PageMapperImpl
import ca.sxxxi.titter.data.utils.mappers.UserSearchMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
	@Provides
	fun userMapper(): UserMapper {
		return UserMapperImpl()
	}

	@Provides
	fun postMapper(): PostMapper {
		return PostMapperImpl(userMapper())
	}

	@Provides
	fun commentMapper(userMapper: UserMapper): CommentMapper {
		return CommentMapperImpl(userMapper = userMapper)
	}

	@Provides
	fun userSearchMapper(userMapper: UserMapper): NDMapper<List<UserSearchResult>, List<UserSearchItem>> {
		return UserSearchMapperImpl(userMapper = userMapper)
	}

	@Provides
	fun pageMapper(
		nToDMapper: NDMapper<List<UserSearchResult>, List<UserSearchItem>>
	): PageMapper<List<UserSearchResult>, List<UserSearchItem>> {
		return PageMapperImpl(nToDMapper)
	}
}