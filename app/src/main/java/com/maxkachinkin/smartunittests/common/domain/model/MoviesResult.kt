package com.maxkachinkin.smartunittests.common.domain.model

data class MoviesResult(
    val movies: List<Movie>,
    val isFromCache: Boolean
)
