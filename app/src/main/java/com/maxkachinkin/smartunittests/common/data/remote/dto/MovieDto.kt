package com.maxkachinkin.smartunittests.common.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("overview")
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerialName("vote_average")
    val voteAverage: Double
)
