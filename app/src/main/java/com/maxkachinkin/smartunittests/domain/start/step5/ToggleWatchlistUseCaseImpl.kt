package com.maxkachinkin.smartunittests.domain.start.step5

import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository

/**
 * TEST-FIRST EXAMPLE: This class starts empty.
 * Write the tests first, then implement to make them pass.
 */
class ToggleWatchlistUseCaseImpl(
    private val movieRepository: MovieRepository
) : ToggleWatchlistUseCase {

    override suspend fun invoke(movieId: Int, currentIsInWatchlist: Boolean) {
        TODO("Implement after writing tests first")
    }
}
