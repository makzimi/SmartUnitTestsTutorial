package com.maxkachinkin.smartunittests.common.data.remote

import android.util.Log
import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreListResponseDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDetailsDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

private const val TAG = "MovieWatchlist"

interface RemoteMovieDataSource {
    suspend fun getPopularMovies(): List<MovieDto>
    suspend fun getMovieDetails(movieId: Int): MovieDetailsDto
    suspend fun getGenres(): List<GenreDto>
}

class RemoteMovieDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val apiKey: String
) : RemoteMovieDataSource {

    override suspend fun getPopularMovies(): List<MovieDto> {
        val url = "${baseUrl}movie/popular"
        Log.d(TAG, "GET $url")
        return try {
            val response = httpClient.get(url) {
                parameter("api_key", apiKey)
            }
            if (response.status.value != 200) {
                val body = response.bodyAsText()
                Log.e(TAG, "getPopularMovies failed: HTTP ${response.status.value}, body=$body")
                throw HttpApiException(response.status.value, body)
            }
            val dto: MovieListResponseDto = response.body()
            Log.d(TAG, "getPopularMovies success: ${dto.results.size} movies")
            dto.results
        } catch (e: Exception) {
            Log.e(TAG, "getPopularMovies error: ${e::class.simpleName} - ${e.message}", e)
            throw e
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetailsDto {
        val url = "${baseUrl}movie/$movieId"
        Log.d(TAG, "GET $url")
        return try {
            val response = httpClient.get(url) {
                parameter("api_key", apiKey)
            }
            if (response.status.value != 200) {
                val body = response.bodyAsText()
                Log.e(TAG, "getMovieDetails($movieId) failed: HTTP ${response.status.value}, body=$body")
                throw HttpApiException(response.status.value, body)
            }
            val dto: MovieDetailsDto = response.body()
            Log.d(TAG, "getMovieDetails($movieId) success: ${dto.title}")
            dto
        } catch (e: Exception) {
            Log.e(TAG, "getMovieDetails($movieId) error: ${e::class.simpleName} - ${e.message}", e)
            throw e
        }
    }

    override suspend fun getGenres(): List<GenreDto> {
        val url = "${baseUrl}genre/movie/list"
        Log.d(TAG, "GET $url")
        return try {
            val response = httpClient.get(url) {
                parameter("api_key", apiKey)
            }
            if (response.status.value != 200) {
                val body = response.bodyAsText()
                Log.e(TAG, "getGenres failed: HTTP ${response.status.value}, body=$body")
                throw HttpApiException(response.status.value, body)
            }
            val dto: GenreListResponseDto = response.body()
            Log.d(TAG, "getGenres success: ${dto.genres.size} genres")
            dto.genres
        } catch (e: Exception) {
            Log.e(TAG, "getGenres error: ${e::class.simpleName} - ${e.message}", e)
            throw e
        }
    }
}
