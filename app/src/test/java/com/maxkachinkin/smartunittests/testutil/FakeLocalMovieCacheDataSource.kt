package com.maxkachinkin.smartunittests.testutil

import com.maxkachinkin.smartunittests.common.data.local.LocalMovieCacheDataSource
import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDto

class FakeLocalMovieCacheDataSource : LocalMovieCacheDataSource {

    private var movies: List<MovieDto>? = null
    private var genres: List<GenreDto>? = null
    private var timestamp: Long = 0L

    override suspend fun getMovies(): List<MovieDto>? = movies

    override suspend fun saveMovies(movies: List<MovieDto>) {
        this.movies = movies
        this.timestamp = System.currentTimeMillis()
    }

    override suspend fun getGenres(): List<GenreDto>? = genres

    override suspend fun saveGenres(genres: List<GenreDto>) {
        this.genres = genres
    }

    override suspend fun getTimestamp(): Long = timestamp
}
