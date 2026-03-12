package com.maxkachinkin.smartunittests.presentation.finish.moviedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository
import com.maxkachinkin.smartunittests.common.domain.api.MarkAsWatchedUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleFavoriteUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import com.maxkachinkin.smartunittests.common.presentation.ErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "MovieWatchlist"

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
        loadDetails()
    }

    fun onToggleFavorite(currentIsFavorite: Boolean) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(movieId, currentIsFavorite)
                loadDetails()
            } catch (e: Exception) {
                Log.e(TAG, "onToggleFavorite($movieId) failed: ${e.message}", e)
            }
        }
    }

    fun onMarkAsWatched() {
        viewModelScope.launch {
            try {
                markAsWatchedUseCase(movieId)
                loadDetails()
            } catch (e: Exception) {
                Log.e(TAG, "onMarkAsWatched($movieId) failed: ${e.message}", e)
            }
        }
    }

    fun onToggleWatchlist() {
        viewModelScope.launch {
            try {
                toggleWatchlistUseCase(movieId)
                loadDetails()
            } catch (e: Exception) {
                Log.e(TAG, "onToggleWatchlist($movieId) failed: ${e.message}", e)
            }
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            try {
                val movie = movieRepository.getMovieDetails(movieId)
                _state.value = MovieDetailsScreenState.Content(movie = movie)
            } catch (e: Exception) {
                Log.e(TAG, "loadDetails($movieId) failed: ${e.message}", e)
                _state.value = MovieDetailsScreenState.Error(
                    message = ErrorMapper.toUserMessage(e)
                )
            }
        }
    }
}
