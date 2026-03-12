package com.maxkachinkin.smartunittests.di

import androidx.lifecycle.ViewModel
import com.maxkachinkin.smartunittests.presentation.finish.movielist.MovieListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    abstract fun bindMovieListViewModel(viewModel: MovieListViewModel): ViewModel
}
