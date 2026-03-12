package com.maxkachinkin.smartunittests.presentation.start.step6.finish

import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.domain.finish.MarkAsWatchedUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleFavoriteUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleWatchlistUseCaseImpl
import com.maxkachinkin.smartunittests.presentation.start.step6.MovieDetailsScreenState
import com.maxkachinkin.smartunittests.presentation.start.step6.MovieDetailsViewModel
import com.maxkachinkin.smartunittests.testutil.FakeMovieRepository
import com.maxkachinkin.smartunittests.testutil.MainDispatcherRule
import com.maxkachinkin.smartunittests.testutil.TestMovieData.MOVIE_1
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * TEST-FIRST EXAMPLE
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMovieRepository

    @Before
    fun setup() {
        repository = FakeMovieRepository()
    }

    private fun createViewModel(movieId: Int = MOVIE_1.id): MovieDetailsViewModel {
        return MovieDetailsViewModel(
            movieId = movieId,
            movieRepository = repository,
            toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(repository),
            markAsWatchedUseCase = MarkAsWatchedUseCaseImpl(repository),
            toggleWatchlistUseCase = ToggleWatchlistUseCaseImpl(repository)
        )
    }

    @Test
    fun `loads movie details on init`() = runTest {
        movieExists(MOVIE_1)

        val sut = createViewModel()
        advanceUntilIdle()

        val movie = resultMovie(sut)
        assertEquals("Action Movie", movie.title)
    }

    @Test
    fun `shows error when loading fails`() = runTest {
        repositoryThrows(RuntimeException("Network error"))

        val sut = createViewModel()
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieDetailsScreenState.Error)
    }

    @Test
    fun `toggles favorite`() = runTest {
        movieExists(MOVIE_1)
        val sut = createViewModel()
        advanceUntilIdle()

        sut.onToggleFavorite(currentIsFavorite = false)
        advanceUntilIdle()

        val movie = resultMovie(sut)
        assertTrue(movie.isFavorite)
    }

    @Test
    fun `marks as watched and removes from watchlist`() = runTest {
        movieInWatchList(MOVIE_1)
        val sut = createViewModel()
        advanceUntilIdle()

        sut.onMarkAsWatched()
        advanceUntilIdle()

        val movie = resultMovie(sut)
        assertTrue(movie.isWatched)
        assertFalse(movie.isInWatchlist)
    }

    @Test
    fun `toggles watchlist`() = runTest {
        movieExists(MOVIE_1)
        val sut = createViewModel()
        advanceUntilIdle()

        sut.onToggleWatchlist()
        advanceUntilIdle()

        val movie = resultMovie(sut)
        assertTrue(movie.isInWatchlist)
    }

    private fun movieExists(movie: Movie) {
        repository.setMovies(listOf(movie))
    }

    private fun movieInWatchList(movie: Movie) {
        repository.setMovies(listOf(movie.copy(isInWatchlist = true)))
    }

    private fun repositoryThrows(exception: Exception) {
        repository.setShouldThrow(exception)
    }

    private fun resultMovie(viewModel: MovieDetailsViewModel): Movie =
        (viewModel.state.value as MovieDetailsScreenState.Content).movie
}
