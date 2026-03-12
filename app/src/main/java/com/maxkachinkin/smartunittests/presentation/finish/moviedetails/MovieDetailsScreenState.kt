package com.maxkachinkin.smartunittests.presentation.finish.moviedetails

import com.maxkachinkin.smartunittests.common.domain.model.Movie

sealed interface MovieDetailsScreenState {

    data object Loading : MovieDetailsScreenState

    data class Content(
        val movie: Movie
    ) : MovieDetailsScreenState

    data class Error(
        val message: String
    ) : MovieDetailsScreenState
}
