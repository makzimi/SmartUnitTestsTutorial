package com.maxkachinkin.smartunittests.presentation.start.step3

import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption
import com.maxkachinkin.smartunittests.domain.start.BuildMovieScreenUseCase
import com.maxkachinkin.smartunittests.presentation.start.MovieListScreenState
import com.maxkachinkin.smartunittests.testutil.TestMovieData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * BAD TEST EXAMPLE
 */
class BuildMovieScreenUseCaseTest {

    // too many mocks
    private val remoteDataSource: RemoteMovieDataSource = mockk()
    private val localCacheDataSource: LocalMovieCacheDataSource = mockk(relaxed = true)
    private val inMemoryCacheDataSource: InMemoryMovieCacheDataSource = mockk(relaxed = true)
    private val userStateDataSource: UserMovieStateDataSource = mockk()
    private val mapper = MovieDomainMapper(imageBaseUrl = "https://image.tmdb.org/t/p/w500")

    private lateinit var sut: BuildMovieScreenUseCase

    @Before
    fun setup() {
        sut = BuildMovieScreenUseCase(
            remoteDataSource = remoteDataSource,
            localCacheDataSource = localCacheDataSource,
            inMemoryCacheDataSource = inMemoryCacheDataSource,
            userStateDataSource = userStateDataSource,
            mapper = mapper
        )
    }

    @Test
    fun `execute returns content when remote data is available`() = runTest {
        every { inMemoryCacheDataSource.getMovies() } returns null
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { localCacheDataSource.getGenres() } returns null
        coEvery { remoteDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns mapOf(
            1 to MovieUserState(isFavorite = true)
        )

        val result = sut.execute(MovieCategory.ALL, MovieSortOption.DEFAULT)

        assertTrue(result is MovieListScreenState.Content)
        val content = result as MovieListScreenState.Content
        assertEquals(3, content.movies.size)
        assertTrue(content.movies[0].isFavorite)
        assertEquals("Action", content.movies[0].genreNames[0])

        coVerify { localCacheDataSource.saveMovies(any()) }
        coVerify { localCacheDataSource.saveGenres(any()) }
    }

    @Test
    fun `execute returns only favorites when category is FAVORITES`() = runTest {
        every { inMemoryCacheDataSource.getMovies() } returns null
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { localCacheDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns mapOf(
            1 to MovieUserState(isFavorite = true),
            3 to MovieUserState(isFavorite = true)
        )

        val result = sut.execute(MovieCategory.FAVORITES, MovieSortOption.DEFAULT)

        assertTrue(result is MovieListScreenState.Content)
        assertEquals(2, (result as MovieListScreenState.Content).movies.size)
    }

    @Test
    fun `execute returns empty when no movies match category`() = runTest {
        every { inMemoryCacheDataSource.getMovies() } returns null
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { localCacheDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns emptyMap()

        val result = sut.execute(MovieCategory.FAVORITES, MovieSortOption.DEFAULT)

        assertTrue(result is MovieListScreenState.Empty)
    }

    @Test
    fun `execute returns error when everything fails`() = runTest {
        every { inMemoryCacheDataSource.getMovies() } returns null
        coEvery { remoteDataSource.getPopularMovies() } throws RuntimeException("Network error")

        val result = sut.execute(MovieCategory.ALL, MovieSortOption.DEFAULT)

        assertTrue(result is MovieListScreenState.Error)
    }

    @Test
    fun `execute sorts by rating`() = runTest {
        every { inMemoryCacheDataSource.getMovies() } returns null
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { localCacheDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns emptyMap()

        val result = sut.execute(MovieCategory.ALL, MovieSortOption.BY_RATING)

        assertTrue(result is MovieListScreenState.Content)
        val movies = (result as MovieListScreenState.Content).movies
        assertEquals(8.2, movies[0].rating, 0.01)
        assertEquals(7.5, movies[1].rating, 0.01)
        assertEquals(6.8, movies[2].rating, 0.01)
    }
}
