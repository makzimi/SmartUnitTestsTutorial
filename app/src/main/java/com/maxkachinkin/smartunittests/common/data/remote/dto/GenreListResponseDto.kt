package com.maxkachinkin.smartunittests.common.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreListResponseDto(
    @SerialName("genres")
    val genres: List<GenreDto>
)
