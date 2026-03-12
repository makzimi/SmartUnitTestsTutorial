package com.maxkachinkin.smartunittests

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maxkachinkin.smartunittests.di.ViewModelFactory
import com.maxkachinkin.smartunittests.presentation.finish.moviedetails.MovieDetailsViewModel
import com.maxkachinkin.smartunittests.presentation.finish.moviedetails.MovieDetailsViewModelFactory
import com.maxkachinkin.smartunittests.presentation.finish.moviedetails.ui.MovieDetailsScreenContent
import com.maxkachinkin.smartunittests.presentation.finish.movielist.MovieListViewModel
import com.maxkachinkin.smartunittests.presentation.finish.movielist.ui.MovieListScreenContent
import com.maxkachinkin.smartunittests.presentation.start.MovieListViewModelFactory
import com.maxkachinkin.smartunittests.presentation.start.ui.StartMovieListScreenContent
import kotlinx.serialization.Serializable

@Serializable
data object MovieListRoute

@Serializable
data class MovieDetailsRoute(val movieId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    viewModelFactory: ViewModelFactory,
    movieDetailsViewModelFactory: MovieDetailsViewModelFactory,
    startViewModelFactory: MovieListViewModelFactory
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState().value
            val isDetails = navBackStackEntry?.destination?.route
                ?.contains(MovieDetailsRoute::class.qualifiedName.orEmpty()) == true

            TopAppBar(
                title = {
                    Text(if (isDetails) "Movie Details" else "Movie Watchlist")
                },
                navigationIcon = {
                    if (isDetails) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieListRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<MovieListRoute> {
                if (AppConfig.USE_FINISH) {
                    val viewModel = viewModel<MovieListViewModel>(factory = viewModelFactory)
                    MovieListScreenContent(
                        viewModel = viewModel,
                        onMovieClick = { movieId ->
                            navController.navigate(MovieDetailsRoute(movieId = movieId))
                        }
                    )
                } else {
                    val viewModel = viewModel<com.maxkachinkin.smartunittests.presentation.start.step3.MovieListViewModel>(
                        factory = startViewModelFactory
                    )
                    StartMovieListScreenContent(
                        viewModel = viewModel,
                        onMovieClick = { movieId ->
                            navController.navigate(MovieDetailsRoute(movieId = movieId))
                        }
                    )
                }
            }

            composable<MovieDetailsRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<MovieDetailsRoute>()
                val viewModel = viewModel<MovieDetailsViewModel>(
                    factory = movieDetailsViewModelFactory.create(route.movieId)
                )
                MovieDetailsScreenContent(viewModel = viewModel)
            }
        }
    }
}
