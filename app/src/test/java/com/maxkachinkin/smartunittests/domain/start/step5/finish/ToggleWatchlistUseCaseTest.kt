package com.maxkachinkin.smartunittests.domain.start.step5.finish

import com.maxkachinkin.smartunittests.domain.start.step5.ToggleWatchlistUseCaseImpl
import com.maxkachinkin.smartunittests.testutil.FakeMovieRepository
import com.maxkachinkin.smartunittests.testutil.TestMovieData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TEST-FIRST EXAMPLE: These tests are written BEFORE the implementation.
 *
 * The tests define the expected behavior:
 * 1. If not in watchlist -> add to watchlist
 * 2. If in watchlist -> remove from watchlist
 *
 * The implementation in ToggleWatchlistUseCaseImpl starts as TODO().
 * Fill it in to make these tests pass.
 */
class ToggleWatchlistUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var sut: ToggleWatchlistUseCaseImpl

    @Before
    fun setup() {
        repository = FakeMovieRepository()
        sut = ToggleWatchlistUseCaseImpl(repository)
    }

    @Test
    fun `adds movie to watchlist when not currently in watchlist`() = runTest {
        repository.setMovies(listOf(TestMovieData.MOVIE_1))

        sut(movieId = 1, currentIsInWatchlist = false)

        val movie = repository.getMovieDetails(1)
        assertTrue(movie.isInWatchlist)
    }

    @Test
    fun `removes movie from watchlist when currently in watchlist`() = runTest {
        repository.setMovies(
            listOf(TestMovieData.MOVIE_1.copy(isInWatchlist = true))
        )

        sut(movieId = 1, currentIsInWatchlist = true)

        val movie = repository.getMovieDetails(1)
        assertFalse(movie.isInWatchlist)
    }
}
