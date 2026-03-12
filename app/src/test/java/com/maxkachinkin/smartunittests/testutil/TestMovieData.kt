package com.maxkachinkin.smartunittests.testutil

import com.maxkachinkin.smartunittests.common.data.remote.dto.GenreDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDetailsDto
import com.maxkachinkin.smartunittests.common.data.remote.dto.MovieDto
import com.maxkachinkin.smartunittests.common.domain.model.Movie

object TestMovieData {

    val ACTION_GENRE = GenreDto(id = 28, name = "Action")
    val COMEDY_GENRE = GenreDto(id = 35, name = "Comedy")
    val DRAMA_GENRE = GenreDto(id = 18, name = "Drama")

    val GENRE_LIST = listOf(ACTION_GENRE, COMEDY_GENRE, DRAMA_GENRE)
    val GENRE_MAP = mapOf(28 to "Action", 35 to "Comedy", 18 to "Drama")

    val MOVIE_DTO_1 = MovieDto(
        id = 1,
        title = "Action Movie",
        overview = "An exciting action movie",
        posterPath = "/action.jpg",
        releaseDate = "2024-01-15",
        genreIds = listOf(28),
        voteAverage = 7.5
    )

    val MOVIE_DTO_2 = MovieDto(
        id = 2,
        title = "Comedy Movie",
        overview = "A funny comedy movie",
        posterPath = "/comedy.jpg",
        releaseDate = "2023-06-20",
        genreIds = listOf(35),
        voteAverage = 6.8
    )

    val MOVIE_DTO_3 = MovieDto(
        id = 3,
        title = "Drama Movie",
        overview = "A dramatic drama movie",
        posterPath = "/drama.jpg",
        releaseDate = "2022-11-10",
        genreIds = listOf(18),
        voteAverage = 8.2
    )

    val MOVIE_DTOS = listOf(MOVIE_DTO_1, MOVIE_DTO_2, MOVIE_DTO_3)

    val MOVIE_DETAILS_DTO = MovieDetailsDto(
        id = 1,
        title = "Action Movie",
        overview = "An exciting action movie",
        posterPath = "/action.jpg",
        releaseDate = "2024-01-15",
        genres = listOf(ACTION_GENRE),
        voteAverage = 7.5
    )

    fun movie(
        id: Int = 1,
        title: String = "Action Movie",
        overview: String = "An exciting action movie",
        posterUrl: String = "https://image.tmdb.org/t/p/w500/action.jpg",
        year: Int = 2024,
        genreNames: List<String> = listOf("Action"),
        rating: Double = 7.5,
        isFavorite: Boolean = false,
        isWatched: Boolean = false,
        isInWatchlist: Boolean = false
    ) = Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        year = year,
        genreNames = genreNames,
        rating = rating,
        isFavorite = isFavorite,
        isWatched = isWatched,
        isInWatchlist = isInWatchlist
    )

    val MOVIE_1 = movie(id = 1, title = "Action Movie", year = 2024, rating = 7.5, genreNames = listOf("Action"))
    val MOVIE_2 = movie(id = 2, title = "Comedy Movie", year = 2023, rating = 6.8, genreNames = listOf("Comedy"), posterUrl = "https://image.tmdb.org/t/p/w500/comedy.jpg", overview = "A funny comedy movie")
    val MOVIE_3 = movie(id = 3, title = "Drama Movie", year = 2022, rating = 8.2, genreNames = listOf("Drama"), posterUrl = "https://image.tmdb.org/t/p/w500/drama.jpg", overview = "A dramatic drama movie")

    val MOVIES = listOf(MOVIE_1, MOVIE_2, MOVIE_3)
}
