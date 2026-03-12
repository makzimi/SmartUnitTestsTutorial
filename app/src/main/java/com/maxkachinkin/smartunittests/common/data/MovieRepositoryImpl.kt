package com.maxkachinkin.smartunittests.common.data

import android.util.Log
import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreDto
import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MoviesResult
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository
import javax.inject.Inject

private const val TAG = "MovieWatchlist"

class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localCacheDataSource: LocalMovieCacheDataSource,
    private val inMemoryCacheDataSource: InMemoryMovieCacheDataSource,
    private val userStateDataSource: UserMovieStateDataSource,
    private val mapper: MovieDomainMapper
) : MovieRepository {

    override suspend fun getMovies(): MoviesResult {
        val inMemory = inMemoryCacheDataSource.getMovies()
        if (inMemory != null) {
            Log.d(TAG, "getMovies: returning ${inMemory.size} movies from in-memory cache")
            val refreshed = mergeWithUserStates(inMemory)
            return MoviesResult(movies = refreshed, isFromCache = false)
        }

        return try {
            Log.d(TAG, "getMovies: fetching from remote...")
            val remoteDtos = remoteDataSource.getPopularMovies()
            val genreDtos = loadGenres()
            val genreMap = mapper.buildGenreMap(genreDtos)
            val userStates = userStateDataSource.getAllStates()

            localCacheDataSource.saveMovies(remoteDtos)

            val movies = remoteDtos.map { dto ->
                val state = userStates[dto.id] ?: MovieUserState()
                mapper.fromDto(dto, genreMap, state)
            }

            inMemoryCacheDataSource.saveMovies(movies)
            Log.d(TAG, "getMovies: fetched ${movies.size} movies from remote")
            MoviesResult(movies = movies, isFromCache = false)
        } catch (e: Exception) {
            Log.e(TAG, "getMovies: remote failed, trying local cache. Error: ${e.message}", e)
            val cachedDtos = localCacheDataSource.getMovies()
            if (cachedDtos != null) {
                val genreDtos = localCacheDataSource.getGenres() ?: emptyList()
                val genreMap = mapper.buildGenreMap(genreDtos)
                val userStates = userStateDataSource.getAllStates()

                val movies = cachedDtos.map { dto ->
                    val state = userStates[dto.id] ?: MovieUserState()
                    mapper.fromDto(dto, genreMap, state)
                }
                Log.d(TAG, "getMovies: returning ${movies.size} movies from local cache")
                MoviesResult(movies = movies, isFromCache = true)
            } else {
                Log.e(TAG, "getMovies: no local cache available, propagating error")
                throw e
            }
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Movie {
        return try {
            Log.d(TAG, "getMovieDetails($movieId): fetching from remote...")
            val detailsDto = remoteDataSource.getMovieDetails(movieId)
            val userState = userStateDataSource.getState(movieId)
            val movie = mapper.fromDetailsDto(detailsDto, userState)
            Log.d(TAG, "getMovieDetails($movieId): success - ${movie.title}")
            movie
        } catch (e: Exception) {
            Log.e(TAG, "getMovieDetails($movieId): failed - ${e.message}", e)
            throw e
        }
    }

    override suspend fun setFavorite(movieId: Int, isFavorite: Boolean) {
        Log.d(TAG, "setFavorite($movieId, $isFavorite)")
        userStateDataSource.setFavorite(movieId, isFavorite)
        inMemoryCacheDataSource.clear()
    }

    override suspend fun setWatched(movieId: Int, isWatched: Boolean) {
        Log.d(TAG, "setWatched($movieId, $isWatched)")
        userStateDataSource.setWatched(movieId, isWatched)
        inMemoryCacheDataSource.clear()
    }

    override suspend fun setInWatchlist(movieId: Int, isInWatchlist: Boolean) {
        Log.d(TAG, "setInWatchlist($movieId, $isInWatchlist)")
        userStateDataSource.setInWatchlist(movieId, isInWatchlist)
        inMemoryCacheDataSource.clear()
    }

    private suspend fun loadGenres(): List<GenreDto> {
        val cached = localCacheDataSource.getGenres()
        if (cached != null) {
            Log.d(TAG, "loadGenres: returning ${cached.size} genres from cache")
            return cached
        }

        Log.d(TAG, "loadGenres: fetching from remote...")
        val remote = remoteDataSource.getGenres()
        localCacheDataSource.saveGenres(remote)
        return remote
    }

    private suspend fun mergeWithUserStates(movies: List<Movie>): List<Movie> {
        val userStates = userStateDataSource.getAllStates()
        return movies.map { movie ->
            val state = userStates[movie.id]
            if (state != null) {
                movie.copy(
                    isFavorite = state.isFavorite,
                    isWatched = state.isWatched,
                    isInWatchlist = state.isInWatchlist
                )
            } else {
                movie
            }
        }
    }
}
