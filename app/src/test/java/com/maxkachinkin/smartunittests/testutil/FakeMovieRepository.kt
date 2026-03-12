package com.maxkachinkin.smartunittests.testutil

import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MoviesResult
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository

class FakeMovieRepository : MovieRepository {

    private var movies = mutableListOf<Movie>()
    private var isFromCache = false
    private var shouldThrow: Exception? = null

    fun setMovies(movieList: List<Movie>) {
        movies = movieList.toMutableList()
    }

    fun setIsFromCache(fromCache: Boolean) {
        isFromCache = fromCache
    }

    fun setShouldThrow(exception: Exception?) {
        shouldThrow = exception
    }

    override suspend fun getMovies(): MoviesResult {
        shouldThrow?.let { throw it }
        return MoviesResult(movies = movies.toList(), isFromCache = isFromCache)
    }

    override suspend fun getMovieDetails(movieId: Int): Movie {
        shouldThrow?.let { throw it }
        return movies.first { it.id == movieId }
    }

    override suspend fun setFavorite(movieId: Int, isFavorite: Boolean) {
        movies = movies.map {
            if (it.id == movieId) it.copy(isFavorite = isFavorite) else it
        }.toMutableList()
    }

    override suspend fun setWatched(movieId: Int, isWatched: Boolean) {
        movies = movies.map {
            if (it.id == movieId) it.copy(isWatched = isWatched) else it
        }.toMutableList()
    }

    override suspend fun setInWatchlist(movieId: Int, isInWatchlist: Boolean) {
        movies = movies.map {
            if (it.id == movieId) it.copy(isInWatchlist = isInWatchlist) else it
        }.toMutableList()
    }
}
