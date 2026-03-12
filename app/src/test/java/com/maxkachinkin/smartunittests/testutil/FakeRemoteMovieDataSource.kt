package com.maxkachinkin.smartunittests.testutil

import com.maxkachinkin.smartunittests.common.data.remote.RemoteMovieDataSource
import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDetailsDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDto

class FakeRemoteMovieDataSource : RemoteMovieDataSource {

    var movies: List<MovieDto> = emptyList()
    var movieDetails: MovieDetailsDto? = null
    var genres: List<GenreDto> = emptyList()
    var shouldThrow: Exception? = null

    override suspend fun getPopularMovies(): List<MovieDto> {
        shouldThrow?.let { throw it }
        return movies
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetailsDto {
        shouldThrow?.let { throw it }
        return movieDetails ?: throw NoSuchElementException("No details for movie $movieId")
    }

    override suspend fun getGenres(): List<GenreDto> {
        shouldThrow?.let { throw it }
        return genres
    }
}
