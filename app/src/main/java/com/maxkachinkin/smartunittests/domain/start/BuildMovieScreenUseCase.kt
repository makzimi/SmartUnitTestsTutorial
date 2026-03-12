package com.maxkachinkin.smartunittests.domain.start

import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption
import com.maxkachinkin.smartunittests.presentation.start.MovieListScreenState

/**
 * BAD EXAMPLE
 */
class BuildMovieScreenUseCase(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localCacheDataSource: LocalMovieCacheDataSource,
    private val inMemoryCacheDataSource: InMemoryMovieCacheDataSource,
    private val userStateDataSource: UserMovieStateDataSource,
    private val mapper: MovieDomainMapper
) {
    suspend fun execute(
        category: MovieCategory,
        sortOption: MovieSortOption
    ): MovieListScreenState {
        return try {
            val movies = loadMovies()

            val filtered = when (category) {
                MovieCategory.ALL -> movies
                MovieCategory.FAVORITES -> movies.filter { it.isFavorite }
                MovieCategory.WATCHED -> movies.filter { it.isWatched }
                MovieCategory.WATCHLIST -> movies.filter { it.isInWatchlist }
            }

            val sorted = when (sortOption) {
                MovieSortOption.DEFAULT -> filtered
                MovieSortOption.BY_RATING -> filtered.sortedByDescending { it.rating }
                MovieSortOption.BY_YEAR -> filtered.sortedByDescending { it.year }
            }

            if (sorted.isEmpty()) {
                MovieListScreenState.Empty
            } else {
                MovieListScreenState.Content(
                    movies = sorted,
                    selectedCategory = category,
                    selectedSortOption = sortOption
                )
            }
        } catch (e: Exception) {
            MovieListScreenState.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun loadMovies(): List<Movie> {
        val cached = inMemoryCacheDataSource.getMovies()
        if (cached != null) {
            val userStates = userStateDataSource.getAllStates()
            return cached.map { movie ->
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

        val remoteDtos = remoteDataSource.getPopularMovies()
        val genreDtos = try {
            val localGenres = localCacheDataSource.getGenres()
            localGenres ?: remoteDataSource.getGenres().also { localCacheDataSource.saveGenres(it) }
        } catch (e: Exception) {
            localCacheDataSource.getGenres() ?: emptyList()
        }

        val genreMap = mapper.buildGenreMap(genreDtos)
        val userStates = userStateDataSource.getAllStates()

        localCacheDataSource.saveMovies(remoteDtos)

        val movies = remoteDtos.map { dto ->
            val state = userStates[dto.id] ?: MovieUserState()
            mapper.fromDto(dto, genreMap, state)
        }

        inMemoryCacheDataSource.saveMovies(movies)
        return movies
    }
}
