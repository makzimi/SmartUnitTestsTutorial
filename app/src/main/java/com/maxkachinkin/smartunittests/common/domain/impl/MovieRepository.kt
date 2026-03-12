package com.maxkachinkin.smartunittests.common.domain.impl

import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.common.domain.model.MoviesResult

interface MovieRepository {

    suspend fun getMovies(): MoviesResult

    suspend fun getMovieDetails(movieId: Int): Movie

    suspend fun setFavorite(movieId: Int, isFavorite: Boolean)

    suspend fun setWatched(movieId: Int, isWatched: Boolean)

    suspend fun setInWatchlist(movieId: Int, isInWatchlist: Boolean)
}
