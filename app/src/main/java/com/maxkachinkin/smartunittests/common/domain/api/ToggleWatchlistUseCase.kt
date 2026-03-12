package com.maxkachinkin.smartunittests.common.domain.api

interface ToggleWatchlistUseCase {

    suspend operator fun invoke(movieId: Int, currentIsInWatchlist: Boolean)
}
