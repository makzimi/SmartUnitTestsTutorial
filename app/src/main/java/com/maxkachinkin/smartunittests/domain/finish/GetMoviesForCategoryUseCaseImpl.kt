package com.maxkachinkin.smartunittests.domain.finish

import com.maxkachinkin.smartunittests.common.domain.api.GetMoviesForCategoryUseCase
import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption
import com.maxkachinkin.smartunittests.common.domain.model.MoviesResult
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository

class GetMoviesForCategoryUseCaseImpl @javax.inject.Inject constructor(
    private val movieRepository: MovieRepository
) : GetMoviesForCategoryUseCase {

    override suspend fun invoke(
        category: MovieCategory,
        sortOption: MovieSortOption
    ): MoviesResult {
        val result = movieRepository.getMovies()
        val filtered = filterByCategory(result.movies, category)
        val sorted = applySorting(filtered, sortOption)
        return result.copy(movies = sorted)
    }

    private fun filterByCategory(movies: List<Movie>, category: MovieCategory): List<Movie> {
        return when (category) {
            MovieCategory.ALL -> movies
            MovieCategory.FAVORITES -> movies.filter { it.isFavorite }
            MovieCategory.WATCHED -> movies.filter { it.isWatched }
            MovieCategory.WATCHLIST -> movies.filter { it.isInWatchlist }
        }
    }

    private fun applySorting(movies: List<Movie>, sortOption: MovieSortOption): List<Movie> {
        return when (sortOption) {
            MovieSortOption.DEFAULT -> movies
            MovieSortOption.BY_RATING -> movies.sortedByDescending { it.rating }
            MovieSortOption.BY_YEAR -> movies.sortedByDescending { it.year }
        }
    }
}
