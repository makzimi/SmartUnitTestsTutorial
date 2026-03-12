package com.maxkachinkin.smartunittests.domain.finish

import com.maxkachinkin.smartunittests.common.domain.api.MarkAsWatchedUseCase
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository

class MarkAsWatchedUseCaseImpl @javax.inject.Inject constructor(
    private val movieRepository: MovieRepository
) : MarkAsWatchedUseCase {

    override suspend fun invoke(movieId: Int, currentIsWatched: Boolean) {
        movieRepository.setWatched(movieId, !currentIsWatched)
        if (!currentIsWatched) {
            movieRepository.setInWatchlist(movieId, false)
        }
    }
}
