package com.maxkachinkin.smartunittests.presentation.finish.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository
import com.maxkachinkin.smartunittests.common.domain.api.MarkAsWatchedUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleFavoriteUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import javax.inject.Inject

class MovieDetailsViewModelFactory @Inject constructor(
    private val movieRepository: MovieRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val markAsWatchedUseCase: MarkAsWatchedUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) {
    fun create(movieId: Int): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass == MovieDetailsViewModel::class.java)
                return MovieDetailsViewModel(
                    movieId = movieId,
                    movieRepository = movieRepository,
                    toggleFavoriteUseCase = toggleFavoriteUseCase,
                    markAsWatchedUseCase = markAsWatchedUseCase,
                    toggleWatchlistUseCase = toggleWatchlistUseCase
                ) as T
            }
        }
    }
}
