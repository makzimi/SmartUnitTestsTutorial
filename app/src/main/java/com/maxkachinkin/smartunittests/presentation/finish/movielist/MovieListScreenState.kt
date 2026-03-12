package com.maxkachinkin.smartunittests.presentation.finish.movielist

import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption

sealed interface MovieListScreenState {

    data object Loading : MovieListScreenState

    data class Content(
        val movies: List<Movie>,
        val selectedCategory: MovieCategory = MovieCategory.ALL,
        val selectedSortOption: MovieSortOption = MovieSortOption.DEFAULT,
        val isRefreshing: Boolean = false,
        val isUsingCachedData: Boolean = false
    ) : MovieListScreenState

    data class Empty(
        val reason: EmptyReason,
        val selectedCategory: MovieCategory = MovieCategory.ALL
    ) : MovieListScreenState

    data class Error(
        val message: String
    ) : MovieListScreenState
}

enum class EmptyReason {
    NO_MOVIES,
    NO_FAVORITES,
    NO_WATCHED,
    NO_WATCHLIST
}
