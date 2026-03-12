package com.maxkachinkin.smartunittests.domain.start.step1.finish

import com.maxkachinkin.smartunittests.domain.finish.MarkAsWatchedUseCaseImpl
import com.maxkachinkin.smartunittests.testutil.FakeMovieRepository
import com.maxkachinkin.smartunittests.testutil.TestMovieData
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
        repository.setMovies(listOf(TestMovieData.MOVIE_1))

        sut(movieId = 1, currentIsWatched = false)

        val movie = repository.getMovieDetails(1)
        Assert.assertTrue(movie.isWatched)
    }

    @Test
    fun `removes movie from watchlist when marking as watched`() = runTest {
        repository.setMovies(
            listOf(TestMovieData.MOVIE_1.copy(isInWatchlist = true))
        )

        sut(movieId = 1, currentIsWatched = false)

        val movie = repository.getMovieDetails(1)
        Assert.assertTrue(movie.isWatched)
        Assert.assertFalse(movie.isInWatchlist)
    }

    @Test
    fun `does not affect other movie statuses`() = runTest {
        repository.setMovies(
            listOf(TestMovieData.MOVIE_1.copy(isFavorite = true, isInWatchlist = true))
        )

        sut(movieId = 1, currentIsWatched = false)

        val movie = repository.getMovieDetails(1)
        Assert.assertTrue(movie.isFavorite)
        Assert.assertTrue(movie.isWatched)
        Assert.assertFalse(movie.isInWatchlist)
    }
}