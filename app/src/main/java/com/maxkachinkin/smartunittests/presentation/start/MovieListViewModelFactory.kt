package com.maxkachinkin.smartunittests.presentation.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource
import com.maxkachinkin.smartunittests.common.data.mapper.MovieDomainMapper
import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.presentation.start.step3.MovieListViewModel
import javax.inject.Inject

class MovieListViewModelFactory @Inject constructor(
    private val remoteDataSource: RemoteMovieDataSource,
    private val localCacheDataSource: LocalMovieCacheDataSource,
    private val inMemoryCacheDataSource: InMemoryMovieCacheDataSource,
    private val userStateDataSource: UserMovieStateDataSource,
    private val mapper: MovieDomainMapper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == MovieListViewModel::class.java)
        return MovieListViewModel(
            remoteDataSource = remoteDataSource,
            localCacheDataSource = localCacheDataSource,
            inMemoryCacheDataSource = inMemoryCacheDataSource,
            userStateDataSource = userStateDataSource,
            mapper = mapper
        ) as T
    }
}
