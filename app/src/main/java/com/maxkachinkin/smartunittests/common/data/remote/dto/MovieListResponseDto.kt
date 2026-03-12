package com.maxkachinkin.smartunittests.common.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieListResponseDto(
    @SerialName("results")
    val results: List<MovieDto>,
    @SerialName("page")
    val page: Int,
    @SerialName("total_pages")
    val totalPages: Int
)
