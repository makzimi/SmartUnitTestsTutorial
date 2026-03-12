package com.maxkachinkin.smartunittests.presentation.start.step4

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxkachinkin.smartunittests.common.domain.api.GetMoviesForCategoryUseCase
import com.maxkachinkin.smartunittests.common.domain.api.MarkAsWatchedUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleFavoriteUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption
import com.maxkachinkin.smartunittests.common.presentation.ErrorMapper
import com.maxkachinkin.smartunittests.presentation.finish.movielist.EmptyReason
import com.maxkachinkin.smartunittests.presentation.finish.movielist.MovieListScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "MovieWatchlist"

class MovieListViewModel(
    private val getMoviesForCategoryUseCase: GetMoviesForCategoryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val markAsWatchedUseCase: MarkAsWatchedUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListScreenState>(MovieListScreenState.Loading)
    val state: StateFlow<MovieListScreenState> = _state.asStateFlow()

    private var currentCategory = MovieCategory.ALL
    private var currentSortOption = MovieSortOption.DEFAULT

    init {
        loadMovies()
    }

    fun onCategorySelected(category: MovieCategory) {
        currentCategory = category
        loadMovies()
    }

    fun onToggleFavorite(movieId: Int, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(movieId, currentIsFavorite)
                loadMovies()
            } catch (e: Exception) {
                Log.e(TAG, "onToggleFavorite($movieId) failed: ${e.message}", e)
            }
        }
    }

    fun onMarkAsWatched(movieId: Int) {
        viewModelScope.launch {
            try {
                markAsWatchedUseCase(movieId)
                loadMovies()
            } catch (e: Exception) {
                Log.e(TAG, "onMarkAsWatched($movieId) failed: ${e.message}", e)
            }
        }
    }

    fun onToggleWatchlist(movieId: Int) {
        viewModelScope.launch {
            try {
                toggleWatchlistUseCase(movieId)
                loadMovies()
            } catch (e: Exception) {
                Log.e(TAG, "onToggleWatchlist($movieId) failed: ${e.message}", e)
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                val result = getMoviesForCategoryUseCase(currentCategory, currentSortOption)

                if (result.movies.isEmpty()) {
                    _state.value = MovieListScreenState.Empty(
                        reason = emptyReasonForCategory(currentCategory),
                        selectedCategory = currentCategory
                    )
                } else {
                    _state.value = MovieListScreenState.Content(
                        movies = result.movies,
                        selectedCategory = currentCategory,
                        selectedSortOption = currentSortOption,
                        isUsingCachedData = result.isFromCache
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadMovies failed: ${e.message}", e)
                _state.value = MovieListScreenState.Error(
                    message = ErrorMapper.toUserMessage(e)
                )
            }
        }
    }

    private fun emptyReasonForCategory(category: MovieCategory): EmptyReason {
        return when (category) {
            MovieCategory.ALL -> EmptyReason.NO_MOVIES
            MovieCategory.FAVORITES -> EmptyReason.NO_FAVORITES
            MovieCategory.WATCHED -> EmptyReason.NO_WATCHED
            MovieCategory.WATCHLIST -> EmptyReason.NO_WATCHLIST
        }
    }
}
