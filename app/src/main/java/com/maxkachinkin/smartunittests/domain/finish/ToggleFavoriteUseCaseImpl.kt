package com.maxkachinkin.smartunittests.domain.finish

import com.maxkachinkin.smartunittests.common.domain.api.ToggleFavoriteUseCase
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository

class ToggleFavoriteUseCaseImpl @javax.inject.Inject constructor(
    private val movieRepository: MovieRepository
) : ToggleFavoriteUseCase {

    override suspend fun invoke(movieId: Int, currentIsFavorite: Boolean) {
        movieRepository.setFavorite(movieId, !currentIsFavorite)
    }
}
