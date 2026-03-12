package com.maxkachinkin.smartunittests.presentation.start

import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption

sealed interface MovieListScreenState {

    data object Loading : MovieListScreenState

    data class Content(
        val movies: List<Movie>,
        val selectedCategory: MovieCategory = MovieCategory.ALL,
        val selectedSortOption: MovieSortOption = MovieSortOption.DEFAULT
    ) : MovieListScreenState

    data object Empty : MovieListScreenState

    data class Error(val message: String) : MovieListScreenState
}
