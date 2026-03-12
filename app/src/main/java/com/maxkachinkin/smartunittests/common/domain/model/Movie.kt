package com.maxkachinkin.smartunittests.common.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val year: Int,
    val genreNames: List<String>,
    val rating: Double,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val isInWatchlist: Boolean = false
)
