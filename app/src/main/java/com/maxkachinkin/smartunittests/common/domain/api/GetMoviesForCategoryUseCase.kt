package com.maxkachinkin.smartunittests.common.domain.api

import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption
import com.maxkachinkin.smartunittests.common.domain.model.MoviesResult

interface GetMoviesForCategoryUseCase {

    suspend operator fun invoke(
        category: MovieCategory,
        sortOption: MovieSortOption = MovieSortOption.DEFAULT
    ): MoviesResult
}
