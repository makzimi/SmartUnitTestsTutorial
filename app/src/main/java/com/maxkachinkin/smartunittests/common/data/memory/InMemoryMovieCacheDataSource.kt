package com.maxkachinkin.smartunittests.common.data.memory

import com.maxkachinkin.smartunittests.common.domain.model.Movie

interface InMemoryMovieCacheDataSource {
    fun getMovies(): List<Movie>?
    fun saveMovies(movies: List<Movie>)
    fun clear()
}

class InMemoryMovieCacheDataSourceImpl : InMemoryMovieCacheDataSource {

    private var cachedMovies: List<Movie>? = null

    override fun getMovies(): List<Movie>? = cachedMovies

    override fun saveMovies(movies: List<Movie>) {
        cachedMovies = movies
    }

    override fun clear() {
        cachedMovies = null
    }
}
