package com.maxkachinkin.smartunittests.presentation.start.step3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption
import com.maxkachinkin.smartunittests.common.presentation.ErrorMapper
import com.maxkachinkin.smartunittests.presentation.start.MovieListScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * BAD EXAMPLE
 */
class MovieListViewModel(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localCacheDataSource: LocalMovieCacheDataSource,
    private val inMemoryCacheDataSource: InMemoryMovieCacheDataSource,
    private val userStateDataSource: UserMovieStateDataSource,
    private val mapper: MovieDomainMapper
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
            userStateDataSource.setFavorite(movieId, !currentIsFavorite)
            inMemoryCacheDataSource.clear()
            loadMovies()
        }
    }

    fun onMarkAsWatched(movieId: Int) {
        viewModelScope.launch {
            userStateDataSource.setWatched(movieId, true)
            userStateDataSource.setInWatchlist(movieId, false)
            inMemoryCacheDataSource.clear()
            loadMovies()
        }
    }

    fun onToggleWatchlist(movieId: Int) {
        viewModelScope.launch {
            val currentState = userStateDataSource.getAllStates()[movieId]
            val isInWatchlist = currentState?.isInWatchlist ?: false
            userStateDataSource.setInWatchlist(movieId, !isInWatchlist)
            inMemoryCacheDataSource.clear()
            loadMovies()
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                val movies = fetchAndMergeMovies()

                val filtered = when (currentCategory) {
                    MovieCategory.ALL -> movies
                    MovieCategory.FAVORITES -> movies.filter { it.isFavorite }
                    MovieCategory.WATCHED -> movies.filter { it.isWatched }
                    MovieCategory.WATCHLIST -> movies.filter { it.isInWatchlist }
                }

                val sorted = when (currentSortOption) {
                    MovieSortOption.DEFAULT -> filtered
                    MovieSortOption.BY_RATING -> filtered.sortedByDescending { it.rating }
                    MovieSortOption.BY_YEAR -> filtered.sortedByDescending { it.year }
                }

                if (sorted.isEmpty()) {
                    _state.value = MovieListScreenState.Empty
                } else {
                    _state.value = MovieListScreenState.Content(
                        movies = sorted,
                        selectedCategory = currentCategory,
                        selectedSortOption = currentSortOption
                    )
                }
            } catch (e: Exception) {
                _state.value = MovieListScreenState.Error(
                    ErrorMapper.toUserMessage(e)
                )
            }
        }
    }

    private suspend fun fetchAndMergeMovies(): List<Movie> {
        val cached = inMemoryCacheDataSource.getMovies()
        if (cached != null) {
            val userStates = userStateDataSource.getAllStates()
            return cached.map { movie ->
                val state = userStates[movie.id]
                if (state != null) {
                    movie.copy(
                        isFavorite = state.isFavorite,
                        isWatched = state.isWatched,
                        isInWatchlist = state.isInWatchlist
                    )
                } else {
                    movie
                }
            }
        }

        val remoteDtos = remoteDataSource.getPopularMovies()
        val genreDtos = try {
            val localGenres = localCacheDataSource.getGenres()
            localGenres ?: remoteDataSource.getGenres().also { localCacheDataSource.saveGenres(it) }
        } catch (e: Exception) {
            localCacheDataSource.getGenres() ?: emptyList()
        }

        val genreMap = mapper.buildGenreMap(genreDtos)
        val userStates = userStateDataSource.getAllStates()

        localCacheDataSource.saveMovies(remoteDtos)

        val movies = remoteDtos.map { dto ->
            val state = userStates[dto.id] ?: MovieUserState()
            mapper.fromDto(dto, genreMap, state)
        }

        inMemoryCacheDataSource.saveMovies(movies)
        return movies
    }
}