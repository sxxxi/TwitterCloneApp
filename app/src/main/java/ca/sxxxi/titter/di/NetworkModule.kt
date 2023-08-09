package ca.sxxxi.titter.di

import ca.sxxxi.titter.data.network.AuthenticationNetworkDataSource
import ca.sxxxi.titter.data.network.CommentsNetworkDataSource
import ca.sxxxi.titter.data.network.PostNetworkDataSource
import ca.sxxxi.titter.data.network.ProfileNetworkDataSource
import ca.sxxxi.titter.data.network.SearchNetworkDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	@Provides
	fun moshi(): Moshi {
		return Moshi.Builder()
			.add(KotlinJsonAdapterFactory())
//			.add(GsonConverterFactory.create())
			.build()
	}

	@Provides
	@TwitterCloneApi
	fun twitterCloneApi(moshi: Moshi): Retrofit {
		return Retrofit.Builder()
			.baseUrl("http://192.168.50.153:8080")
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.build();
	}

	@Provides
	fun authNetworkDatasource(@TwitterCloneApi retrofit: Retrofit): AuthenticationNetworkDataSource {
		return retrofit.create(AuthenticationNetworkDataSource::class.java)
	}

	@Provides
	fun profileNetworkDatasource(@TwitterCloneApi retrofit: Retrofit): ProfileNetworkDataSource {
		return retrofit.create(ProfileNetworkDataSource::class.java)
	}

	@Provides
	fun postNetworkDataSource(@TwitterCloneApi retrofit: Retrofit): PostNetworkDataSource {
		return retrofit.create(PostNetworkDataSource::class.java)
	}

	@Provides
	fun commentsNetworkDataSource(@TwitterCloneApi retrofit: Retrofit): CommentsNetworkDataSource {
		return retrofit.create(CommentsNetworkDataSource::class.java)
	}

	@Provides
	fun searchNetworkDataSource(@TwitterCloneApi retrofit: Retrofit): SearchNetworkDataSource {
		return retrofit.create(SearchNetworkDataSource::class.java)
	}
}