package com.maxkachinkin.smartunittests.domain.start.step5.finish

import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.domain.start.step5.ToggleWatchlistUseCaseImpl
import com.maxkachinkin.smartunittests.testutil.FakeMovieRepository
import com.maxkachinkin.smartunittests.testutil.TestMovieData.MOVIE_1
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * TEST-FIRST EXAMPLE
 */
class ToggleWatchlistUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var sut: ToggleWatchlistUseCaseImpl

    @Before
    fun setup() {
        repository = FakeMovieRepository()

        //sut = ToggleWatchlistUseCaseImpl(repository)
        sut = ToggleWatchlistUseCaseImpl()
    }

    @Test
    fun `adds movie to watchlist when not currently in watchlist`() = runTest {
        movieNotInWatchList(MOVIE_1)

        sut.invoke(movieId = MOVIE_1.id)

        val movie = getResultedMovie(MOVIE_1)
        assertTrue(movie.isInWatchlist)
    }

    @Test
    fun `removes movie from watchlist when currently in watchlist`() = runTest {
        movieInWatchList(MOVIE_1)

        sut.invoke(movieId = MOVIE_1.id)

        val movie = getResultedMovie(MOVIE_1)
        assertFalse(movie.isInWatchlist)
    }

    private fun movieInWatchList(movie: Movie) {
        repository.setMovies(listOf(movie.copy(isInWatchlist = true)))
    }

    private suspend fun getResultedMovie(movie: Movie): Movie =
        repository.getMovieDetails(movie.id)

    private fun movieNotInWatchList(movie: Movie) {
        repository.setMovies(listOf(movie.copy(isInWatchlist = false)))
    }
}