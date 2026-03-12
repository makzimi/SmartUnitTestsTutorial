package com.maxkachinkin.smartunittests.common.data.local

import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDto

interface LocalMovieCacheDataSource {
    suspend fun getMovies(): List<MovieDto>?
    suspend fun saveMovies(movies: List<MovieDto>)
    suspend fun getGenres(): List<GenreDto>?
    suspend fun saveGenres(genres: List<GenreDto>)
    suspend fun getTimestamp(): Long
}

class LocalMovieCacheDataSourceImpl : LocalMovieCacheDataSource {

    private var cachedMovies: List<MovieDto>? = null
    private var cachedGenres: List<GenreDto>? = null
    private var timestamp: Long = 0L

    override suspend fun getMovies(): List<MovieDto>? = cachedMovies

    override suspend fun saveMovies(movies: List<MovieDto>) {
        cachedMovies = movies
        timestamp = System.currentTimeMillis()
    }

    override suspend fun getGenres(): List<GenreDto>? = cachedGenres

    override suspend fun saveGenres(genres: List<GenreDto>) {
        cachedGenres = genres
    }

    override suspend fun getTimestamp(): Long = timestamp
}
