package com.maxkachinkin.smartunittests.common.domain.api

interface MarkAsWatchedUseCase {

    suspend operator fun invoke(movieId: Int, currentIsWatched: Boolean)
}
