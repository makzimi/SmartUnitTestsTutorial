package com.maxkachinkin.smartunittests.domain.finish

import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository

class ToggleWatchlistUseCaseImpl @javax.inject.Inject constructor(
    private val movieRepository: MovieRepository
) : ToggleWatchlistUseCase {

    override suspend fun invoke(movieId: Int, currentIsInWatchlist: Boolean) {
        movieRepository.setInWatchlist(movieId, !currentIsInWatchlist)
    }
}
