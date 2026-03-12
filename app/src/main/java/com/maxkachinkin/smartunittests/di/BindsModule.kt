package com.maxkachinkin.smartunittests.di

import com.maxkachinkin.smartunittests.common.data.MovieRepositoryImpl
import com.maxkachinkin.smartunittests.common.domain.impl.MovieRepository
import com.maxkachinkin.smartunittests.common.domain.api.GetMoviesForCategoryUseCase
import com.maxkachinkin.smartunittests.common.domain.api.MarkAsWatchedUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleFavoriteUseCase
import com.maxkachinkin.smartunittests.common.domain.api.ToggleWatchlistUseCase
import com.maxkachinkin.smartunittests.domain.finish.GetMoviesForCategoryUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.MarkAsWatchedUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleFavoriteUseCaseImpl
import com.maxkachinkin.smartunittests.domain.finish.ToggleWatchlistUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class BindsModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    abstract fun bindGetMoviesForCategoryUseCase(impl: GetMoviesForCategoryUseCaseImpl): GetMoviesForCategoryUseCase

    @Binds
    abstract fun bindToggleFavoriteUseCase(impl: ToggleFavoriteUseCaseImpl): ToggleFavoriteUseCase

    @Binds
    abstract fun bindMarkAsWatchedUseCase(impl: MarkAsWatchedUseCaseImpl): MarkAsWatchedUseCase

    @Binds
    abstract fun bindToggleWatchlistUseCase(impl: ToggleWatchlistUseCaseImpl): ToggleWatchlistUseCase
}
