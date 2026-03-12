package com.maxkachinkin.smartunittests.domain.start.step1.finish

import com.maxkachinkin.smartunittests.domain.finish.MarkAsWatchedUseCaseImpl
import com.maxkachinkin.smartunittests.testutil.FakeMovieRepository
import com.maxkachinkin.smartunittests.testutil.TestMovieData.MOVIE_1
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MarkAsWatchedUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var sut: MarkAsWatchedUseCaseImpl

    @Before
    fun setup() {
        repository = FakeMovieRepository()
        sut = MarkAsWatchedUseCaseImpl(repository)
    }

    @Test
    fun `marks movie as watched`() = runTest {
        repository.setMovies(listOf(MOVIE_1.copy(isWatched = false)))

        sut.invoke(movieId = MOVIE_1.id)

        val movie = repository.getMovieDetails(MOVIE_1.id)
        Assert.assertTrue(movie.isWatched)
    }

    @Test
    fun `removes movie from watchlist when marking as watched`() = runTest {
        repository.setMovies(
            listOf(MOVIE_1.copy(isWatched = false, isInWatchlist = true))
        )

        sut.invoke(movieId = MOVIE_1.id)

        val movie = repository.getMovieDetails(MOVIE_1.id)
        Assert.assertFalse(movie.isInWatchlist)
    }

    @Test
    fun `does not affect favorites movie status`() = runTest {
        repository.setMovies(
            listOf(MOVIE_1.copy(isFavorite = true, isInWatchlist = true))
        )

        sut.invoke(movieId = MOVIE_1.id)

        val movie = repository.getMovieDetails(MOVIE_1.id)
        Assert.assertTrue(movie.isFavorite)
    }
}