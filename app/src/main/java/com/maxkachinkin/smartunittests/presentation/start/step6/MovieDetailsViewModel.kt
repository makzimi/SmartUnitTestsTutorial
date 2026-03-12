package com.maxkachinkin.smartunittests.presentation.start.step6

import androidx.lifecycle.ViewModel
import com.maxkachinkin.smartunittests.common.domain.api.MarkAsWatchedUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleFavoriteUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TEST-FIRST EXAMPLE: This ViewModel starts empty.
 * Write tests first, then implement to make them pass.
 */
class MovieDetailsViewModel(
    private val movieId: Int,
    private val movieRepository: MovieRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val markAsWatchedUseCase: MarkAsWatchedUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MovieDetailsScreenState>(MovieDetailsScreenState.Loading)
    val state: StateFlow<MovieDetailsScreenState> = _state.asStateFlow()

    init {
        TODO("Load movie details")
    }

    fun onToggleFavorite(currentIsFavorite: Boolean) {
        TODO("Toggle favorite and reload")
    }

    fun onMarkAsWatched(currentIsWatched: Boolean) {
        TODO("Mark as watched and reload")
    }

    fun onToggleWatchlist(currentIsInWatchlist: Boolean) {
        TODO("Toggle watchlist and reload")
    }
}
