package com.maxkachinkin.smartunittests.presentation.start.step6.finish

import com.maxkachinkin.smartunittests.domain.finish.MarkAsWatchedUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleFavoriteUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleWatchlistUseCaseImpl
import com.maxkachinkin.smartunittests.presentation.start.step6.MovieDetailsScreenState
import com.maxkachinkin.smartunittests.presentation.start.step6.MovieDetailsViewModel
import com.maxkachinkin.smartunittests.testutil.FakeMovieRepository
import com.maxkachinkin.smartunittests.testutil.MainDispatcherRule
import com.maxkachinkin.smartunittests.testutil.TestMovieData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * TEST-FIRST EXAMPLE: These tests are written BEFORE the ViewModel implementation.
 *
 * The tests define expected behavior:
 * 1. Loads movie details on init
 * 2. Shows error when loading fails
 * 3. Toggles favorite
 * 4. Marks as watched (removes from watchlist)
 * 5. Toggles watchlist
 *
 * Implementation in MovieDetailsViewModel starts as TODO().
 * Fill it in to make these tests pass.
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

    private fun createViewModel(movieId: Int = 1): MovieDetailsViewModel {
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
        repository.setMovies(listOf(TestMovieData.MOVIE_1))

        val sut = createViewModel(movieId = 1)
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieDetailsScreenState.Content)
        assertEquals("Action Movie", (state as MovieDetailsScreenState.Content).movie.title)
    }

    @Test
    fun `shows error when loading fails`() = runTest {
        repository.setShouldThrow(RuntimeException("Network error"))

        val sut = createViewModel(movieId = 1)
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieDetailsScreenState.Error)
    }

    @Test
    fun `toggles favorite`() = runTest {
        repository.setMovies(listOf(TestMovieData.MOVIE_1))
        val sut = createViewModel(movieId = 1)
        advanceUntilIdle()

        sut.onToggleFavorite(currentIsFavorite = false)
        advanceUntilIdle()

        val state = sut.state.value as MovieDetailsScreenState.Content
        assertTrue(state.movie.isFavorite)
    }

    @Test
    fun `marks as watched and removes from watchlist`() = runTest {
        repository.setMovies(
            listOf(TestMovieData.MOVIE_1.copy(isInWatchlist = true))
        )
        val sut = createViewModel(movieId = 1)
        advanceUntilIdle()

        sut.onMarkAsWatched(currentIsWatched = false)
        advanceUntilIdle()

        val state = sut.state.value as MovieDetailsScreenState.Content
        assertTrue(state.movie.isWatched)
        assertTrue(!state.movie.isInWatchlist)
    }

    @Test
    fun `toggles watchlist`() = runTest {
        repository.setMovies(listOf(TestMovieData.MOVIE_1))
        val sut = createViewModel(movieId = 1)
        advanceUntilIdle()

        sut.onToggleWatchlist(currentIsInWatchlist = false)
        advanceUntilIdle()

        val state = sut.state.value as MovieDetailsScreenState.Content
        assertTrue(state.movie.isInWatchlist)
    }
}
