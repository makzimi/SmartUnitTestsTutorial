package com.maxkachinkin.smartunittests.presentation.start.step3

import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.presentation.start.MovieListScreenState
import com.maxkachinkin.smartunittests.presentation.start.step3.MovieListViewModel
import com.maxkachinkin.smartunittests.testutil.MainDispatcherRule
import com.maxkachinkin.smartunittests.testutil.TestMovieData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * BAD TEST EXAMPLE
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 5 mocks
    private val remoteDataSource: RemoteMovieDataSource = mockk()
    private val localCacheDataSource: LocalMovieCacheDataSource = mockk(relaxed = true)
    private val inMemoryCacheDataSource: InMemoryMovieCacheDataSource = mockk(relaxed = true)
    private val userStateDataSource: UserMovieStateDataSource = mockk(relaxed = true)
    private val mapper = MovieDomainMapper(imageBaseUrl = "https://image.tmdb.org/t/p/w500")

    @Before
    fun setup() {
        // Default mocking — even the setup is complex
        every { inMemoryCacheDataSource.getMovies() } returns null
        coEvery { localCacheDataSource.getGenres() } returns TestMovieData.GENRE_LIST
        coEvery { userStateDataSource.getAllStates() } returns emptyMap()
    }

    private fun createViewModel(): MovieListViewModel {
        return MovieListViewModel(
            remoteDataSource = remoteDataSource,
            localCacheDataSource = localCacheDataSource,
            inMemoryCacheDataSource = inMemoryCacheDataSource,
            userStateDataSource = userStateDataSource,
            mapper = mapper
        )
    }

    @Test
    fun `shows content after loading movies`() = runTest {
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS

        val sut = createViewModel()
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieListScreenState.Content)
        assertEquals(3, (state as MovieListScreenState.Content).movies.size)

        // We verify caching — this is an implementation detail!
        coVerify { localCacheDataSource.saveMovies(any()) }
    }

    @Test
    fun `shows error when remote fails`() = runTest {
        coEvery { remoteDataSource.getPopularMovies() } throws RuntimeException("Network error")
        coEvery { localCacheDataSource.getMovies() } returns null

        val sut = createViewModel()
        advanceUntilIdle()

        val state = sut.state.value
        assertTrue(state is MovieListScreenState.Error)
    }

    @Test
    fun `toggles favorite correctly`() = runTest {
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { userStateDataSource.setFavorite(1, true) } just runs

        val sut = createViewModel()
        advanceUntilIdle()

        // After toggling, we need to re-mock ALL the data flow
        // because the ViewModel reloads everything
        coEvery { userStateDataSource.getAllStates() } returns mapOf(
            1 to MovieUserState(isFavorite = true)
        )

        sut.onToggleFavorite(1, false)
        advanceUntilIdle()

        // We verify the interaction — not the outcome
        coVerify { userStateDataSource.setFavorite(1, true) }
        coVerify { inMemoryCacheDataSource.clear() }
    }

    @Test
    fun `marks movie as watched and removes from watchlist`() = runTest {
        coEvery { remoteDataSource.getPopularMovies() } returns TestMovieData.MOVIE_DTOS
        coEvery { userStateDataSource.setWatched(1, true) } just runs
        coEvery { userStateDataSource.setInWatchlist(1, false) } just runs

        val sut = createViewModel()
        advanceUntilIdle()

        coEvery { userStateDataSource.getAllStates() } returns mapOf(
            1 to MovieUserState(isWatched = true)
        )

        sut.onMarkAsWatched(1)
        advanceUntilIdle()

        // Notice how we verify two separate calls — very coupled to implementation
        coVerify { userStateDataSource.setWatched(1, true) }
        coVerify { userStateDataSource.setInWatchlist(1, false) }
    }
}
