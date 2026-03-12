package com.maxkachinkin.smartunittests.di

import com.maxkachinkin.smartunittests.BuildConfig
import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSourceImpl
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSourceImpl
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSourceImpl
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSourceImpl
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    @Provides
    @Singleton
    fun provideRemoteMovieDataSource(httpClient: HttpClient): RemoteMovieDataSource {
        return RemoteMovieDataSourceImpl(
            httpClient = httpClient,
            baseUrl = BuildConfig.TMDB_BASE_URL,
            apiKey = BuildConfig.TMDB_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideLocalMovieCacheDataSource(): LocalMovieCacheDataSource {
        return LocalMovieCacheDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideInMemoryMovieCacheDataSource(): InMemoryMovieCacheDataSource {
        return InMemoryMovieCacheDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideUserMovieStateDataSource(): UserMovieStateDataSource {
        return UserMovieStateDataSourceImpl()
    }

    @Provides
    @Singleton
    fun provideMovieDomainMapper(): MovieDomainMapper {
        return MovieDomainMapper(imageBaseUrl = BuildConfig.TMDB_IMAGE_BASE_URL)
    }
}
