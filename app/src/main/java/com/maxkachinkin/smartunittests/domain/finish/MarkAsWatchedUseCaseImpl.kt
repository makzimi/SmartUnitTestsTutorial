package com.maxkachinkin.smartunittests.domain.finish

import com.maxkachinkin.smartunittests.common.domain.api.MarkAsWatchedUseCase
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository
import javax.inject.Inject

class MarkAsWatchedUseCaseImpl @Inject constructor(
    private val movieRepository: MovieRepository
) : MarkAsWatchedUseCase {

    override suspend fun invoke(movieId: Int) {
        val movie = movieRepository.getMovieDetails(movieId)
        movieRepository.setWatched(movieId, !movie.isWatched)
        if (!movie.isWatched) {
            movieRepository.setInWatchlist(movieId, false)
        }
    }
}
