package com.maxkachinkin.smartunittests.common.data.mapper

import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDetailsDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDto
import com.maxkachinkin.smartunittests.common.domain.model.Movie

class MovieDomainMapper(private val imageBaseUrl: String) {

    fun fromDto(
        dto: MovieDto,
        genres: Map<Int, String>,
        userState: MovieUserState = MovieUserState()
    ): Movie {
        return Movie(
            id = dto.id,
            title = dto.title,
            overview = dto.overview,
            posterUrl = dto.posterPath?.let { "$imageBaseUrl$it" } ?: "",
            year = dto.releaseDate?.take(4)?.toIntOrNull() ?: 0,
            genreNames = dto.genreIds.mapNotNull { genres[it] },
            rating = dto.voteAverage,
            isFavorite = userState.isFavorite,
            isWatched = userState.isWatched,
            isInWatchlist = userState.isInWatchlist
        )
    }

    fun fromDetailsDto(
        dto: MovieDetailsDto,
        userState: MovieUserState = MovieUserState()
    ): Movie {
        return Movie(
            id = dto.id,
            title = dto.title,
            overview = dto.overview,
            posterUrl = dto.posterPath?.let { "$imageBaseUrl$it" } ?: "",
            year = dto.releaseDate?.take(4)?.toIntOrNull() ?: 0,
            genreNames = dto.genres.map(GenreDto::name),
            rating = dto.voteAverage,
            isFavorite = userState.isFavorite,
            isWatched = userState.isWatched,
            isInWatchlist = userState.isInWatchlist
        )
    }

    fun buildGenreMap(genres: List<GenreDto>): Map<Int, String> {
        return genres.associate { it.id to it.name }
    }
}
