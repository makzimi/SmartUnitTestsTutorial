package com.maxkachinkin.smartunittests.presentation.start.step4.finish

import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.domain.finish.GetMoviesForCategoryUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.MarkAsWatchedUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleFavoriteUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleWatchlistUseCaseImpl
import com.maxkachinkin.smartunittests.presentation.finish.movielist.EmptyReason
import com.maxkachinkin.smartunittests.presentation.finish.movielist.MovieListScreenState
import com.maxkachinkin.smartunittests.presentation.start.step4.MovieListViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMovieRepository
    private lateinit var sut: MovieListViewModel

    @Before
    fun setup() {
        repository = FakeMovieRepository()
    }

    private fun createViewModel(): MovieListViewModel {
        return MovieListViewModel(
            getMoviesForCategoryUseCase = GetMoviesForCategoryUseCaseImpl(repository),
            toggleFavoriteUseCase = ToggleFavoriteUseCaseImpl(repository),
            markAsWatchedUseCase = MarkAsWatchedUseCaseImpl(repository),
            toggleWatchlistUseCase = ToggleWatchlistUseCaseImpl(repository)
        )
    }

    @Test
    fun `emits content on success`() = runTest {
        repository.setMovies(TestMovieData.MOVIES)
        sut = createViewModel()
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieListScreenState.Content)
        assertEquals(3, (state as MovieListScreenState.Content).movies.size)
    }

    @Test
    fun `emits empty for empty favorites`() = runTest {
        repository.setMovies(TestMovieData.MOVIES)
        sut = createViewModel()
        advanceUntilIdle()

        sut.onCategorySelected(MovieCategory.FAVORITES)
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieListScreenState.Empty)
        assertEquals(EmptyReason.NO_FAVORITES, (state as MovieListScreenState.Empty).reason)
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        repository.setShouldThrow(RuntimeException("Network failure"))
        sut = createViewModel()
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieListScreenState.Error)
        assertEquals("Network failure", (state as MovieListScreenState.Error).message)
    }

    @Test
    fun `updates state after toggling favorite`() = runTest {
        repository.setMovies(TestMovieData.MOVIES)
        sut = createViewModel()
        advanceUntilIdle()

        sut.onToggleFavorite(movieId = 1, currentIsFavorite = false)
        advanceUntilIdle()

        val state = sut.state.value as MovieListScreenState.Content
        assertTrue(state.movies.first { it.id == 1 }.isFavorite)
    }

    @Test
    fun `updates state after marking movie as watched`() = runTest {
        repository.setMovies(
            listOf(TestMovieData.MOVIE_1.copy(isInWatchlist = true))
        )
        sut = createViewModel()
        advanceUntilIdle()

        sut.onMarkAsWatched(movieId = 1, currentIsWatched = false)
        advanceUntilIdle()

        val state = sut.state.value as MovieListScreenState.Content
        val movie = state.movies.first { it.id == 1 }
        assertTrue(movie.isWatched)
        assertTrue(!movie.isInWatchlist)
    }
}
