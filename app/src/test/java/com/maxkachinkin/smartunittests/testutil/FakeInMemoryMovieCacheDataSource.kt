package com.maxkachinkin.smartunittests.testutil

import com.maxkachinkin.smartunittests.common.data.memory.InMemoryMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.domain.model.Movie

class FakeInMemoryMovieCacheDataSource : InMemoryMovieCacheDataSource {

    private var movies: List<Movie>? = null

    override fun getMovies(): List<Movie>? = movies

    override fun saveMovies(movies: List<Movie>) {
        this.movies = movies
    }

    override fun clear() {
        movies = null
    }
}
