package com.maxkachinkin.smartunittests

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.maxkachinkin.smartunittests.di.ViewModelFactory
import com.maxkachinkin.smartunittests.presentation.finish.moviedetails.MovieDetailsViewModelFactory
import com.maxkachinkin.smartunittests.presentation.start.MovieListViewModelFactory
import com.maxkachinkin.smartunittests.ui.theme.SmartUnitTestsTutorialTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var movieDetailsViewModelFactory: MovieDetailsViewModelFactory

    @Inject
    lateinit var startViewModelFactory: MovieListViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MovieWatchlistApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartUnitTestsTutorialTheme {
                MainNavigation(
                    viewModelFactory = viewModelFactory,
                    movieDetailsViewModelFactory = movieDetailsViewModelFactory,
                    startViewModelFactory = startViewModelFactory
                )
            }
        }
    }
}
