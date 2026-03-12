package com.maxkachinkin.smartunittests.common.domain.api

interface ToggleFavoriteUseCase {

    suspend operator fun invoke(movieId: Int, currentIsFavorite: Boolean)
}
