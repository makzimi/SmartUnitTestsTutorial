package com.maxkachinkin.smartunittests.domain.start.step2.finish

import com.maxkachinkin.smartunittests.common.data.MovieRepositoryImpl
import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.testutil.FakeInMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.testutil.FakeLocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.testutil.FakeRemoteMovieDataSource
import com.maxkachinkin.smartunittests.testutil.FakeUserMovieStateDataSource
import com.maxkachinkin.smartunittests.testutil.TestMovieData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieRepositoryWithFakesTest {

    private lateinit var remoteDataSource: FakeRemoteMovieDataSource
    private lateinit var localCacheDataSource: FakeLocalMovieCacheDataSource
    private lateinit var inMemoryCacheDataSource: FakeInMemoryMovieCacheDataSource
    private lateinit var userStateDataSource: FakeUserMovieStateDataSource
    private val mapper = MovieDomainMapper(imageBaseUrl = "https://image.tmdb.org/t/p/w500")

    private lateinit var sut: MovieRepositoryImpl

    @Before
    fun setup() {
        remoteDataSource = FakeRemoteMovieDataSource()
        localCacheDataSource = FakeLocalMovieCacheDataSource()
        inMemoryCacheDataSource = FakeInMemoryMovieCacheDataSource()
        userStateDataSource = FakeUserMovieStateDataSource()
        sut = MovieRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localCacheDataSource = localCacheDataSource,
            inMemoryCacheDataSource = inMemoryCacheDataSource,
            userStateDataSource = userStateDataSource,
            mapper = mapper
        )
    }

    @Test
    fun `getMovies returns movies from remote on first call`() = runTest {
        remoteDataSource.movies = TestMovieData.MOVIE_DTOS
        remoteDataSource.genres = TestMovieData.GENRE_LIST

        val result = sut.getMovies()

        assertEquals(3, result.movies.size)
        assertFalse(result.isFromCache)
    }

    @Test
    fun `getMovies merges user states with remote movies`() = runTest {
        remoteDataSource.movies = TestMovieData.MOVIE_DTOS
        remoteDataSource.genres = TestMovieData.GENRE_LIST
        userStateDataSource.setState(1, MovieUserState(isFavorite = true))

        val result = sut.getMovies()

        assertTrue(result.movies.first { it.id == 1 }.isFavorite)
        assertFalse(result.movies.first { it.id == 2 }.isFavorite)
    }

    @Test
    fun `getMovies falls back to local cache when remote fails`() = runTest {
        remoteDataSource.shouldThrow = RuntimeException("Network error")
        localCacheDataSource.saveMovies(TestMovieData.MOVIE_DTOS)
        localCacheDataSource.saveGenres(TestMovieData.GENRE_LIST)

        val result = sut.getMovies()

        assertEquals(3, result.movies.size)
        assertTrue(result.isFromCache)
    }

    @Test
    fun `getMovies throws when remote fails and no local cache`() = runTest {
        remoteDataSource.shouldThrow = RuntimeException("Network error")

        try {
            sut.getMovies()
            assertTrue("Should have thrown", false)
        } catch (e: Exception) {
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun `setFavorite updates user state`() = runTest {
        sut.setFavorite(1, true)

        val state = userStateDataSource.getState(1)
        assertTrue(state.isFavorite)
    }

    @Test
    fun `setWatched updates user state and clears memory cache`() = runTest {
        remoteDataSource.movies = TestMovieData.MOVIE_DTOS
        remoteDataSource.genres = TestMovieData.GENRE_LIST
        sut.getMovies() // populate in-memory cache

        sut.setWatched(1, true)

        val state = userStateDataSource.getState(1)
        assertTrue(state.isWatched)
        // in-memory cache should be cleared
        assertEquals(null, inMemoryCacheDataSource.getMovies())
    }

    @Test
    fun `getMovieDetails returns movie from remote`() = runTest {
        remoteDataSource.movieDetails = TestMovieData.MOVIE_DETAILS_DTO

        val movie = sut.getMovieDetails(1)

        assertEquals("Action Movie", movie.title)
        assertEquals(2024, movie.year)
    }
}
