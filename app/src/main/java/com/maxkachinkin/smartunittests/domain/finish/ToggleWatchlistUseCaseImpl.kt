package com.maxkachinkin.smartunittests.domain.finish

import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository

import javax.inject.Inject

class ToggleWatchlistUseCaseImpl @Inject constructor(
    private val movieRepository: MovieRepository
) : ToggleWatchlistUseCase {

    override suspend fun invoke(movieId: Int) {
        val movie = movieRepository.getMovieDetails(movieId)
        movieRepository.setInWatchlist(movieId, !movie.isInWatchlist)
    }
}
