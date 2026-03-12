package com.maxkachinkin.smartunittests.domain.start.step2.finish

import com.maxkachinkin.smartunittests.common.data.MovieRepositoryImpl
import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.testutil.TestMovieData
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Same repository tests but using MockK.
 */
class MovieRepositoryWithMocksTest {

    private val remoteDataSource: RemoteMovieDataSource = mockk()
    private val localCacheDataSource: LocalMovieCacheDataSource = mockk(relaxed = true)
    private val inMemoryCacheDataSource: InMemoryMovieCacheDataSource = mockk(relaxed = true)
    private val userStateDataSource: UserMovieStateDataSource = mockk()
    private val mapper = MovieDomainMapper(imageBaseUrl = "https://image.tmdb.org/t/p/w500")

    private lateinit var sut: MovieRepositoryImpl

    @Before
    fun setup() {
        every { inMemoryCacheDataSource.getMovies() } returns null
        sut = MovieRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localCacheDataSource = localCacheDataSource,
            inMemoryCacheDataSource = inMemoryCacheDataSource,
            userStateDataSource = userStateDataSource,
            mapper = mapper
        )
    }

    @Test
    fun `getMovies fetches from remote and saves to cache`() = runTest {
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { localCacheDataSource.getGenres() } returns null
        coEvery { remoteDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns emptyMap()

        val result = sut.getMovies()

        assertEquals(3, result.movies.size)
        assertFalse(result.isFromCache)
        // implementation details
        coVerify { localCacheDataSource.saveMovies(TestMovieData.MOVIE_DTOS) }
        coVerify { localCacheDataSource.saveGenres(TestMovieData.GENRE_LIST) }
    }

    @Test
    fun `getMovies merges user states`() = runTest {
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { localCacheDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns mapOf(
            1 to MovieUserState(isFavorite = true)
        )

        val result = sut.getMovies()

        assertTrue(result.movies.first { it.id == 1 }.isFavorite)
    }

    @Test
    fun `getMovies falls back to cache on remote failure`() = runTest {
        coEvery { remoteDataSource.getPopularMovies() } throws RuntimeException("Network error")
        coEvery { localCacheDataSource.getMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { localCacheDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns emptyMap()

        val result = sut.getMovies()

        assertEquals(3, result.movies.size)
        assertTrue(result.isFromCache)
    }

    @Test
    fun `setFavorite delegates to user state data source and clears cache`() = runTest {
        coJustRun { userStateDataSource.setFavorite(1, true) }
        justRun { inMemoryCacheDataSource.clear() }

        sut.setFavorite(1, true)

        coVerify { userStateDataSource.setFavorite(1, true) }
        // verifying implementation details like cache clearing
        coVerify { inMemoryCacheDataSource.clear() }
    }
}
