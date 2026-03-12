package com.maxkachinkin.smartunittests.presentation.start.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.presentation.finish.movielist.ui.MovieCard
import com.maxkachinkin.smartunittests.presentation.start.MovieListScreenState
import com.maxkachinkin.smartunittests.presentation.start.step3.MovieListViewModel

@Composable
fun StartMovieListScreenContent(
    viewModel: MovieListViewModel,
    onMovieClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        val selectedCategory = when (val s = state) {
            is MovieListScreenState.Content -> s.selectedCategory
            else -> MovieCategory.ALL
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MovieCategory.entries.toList()) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = {
                        Text(
                            text = when (category) {
                                MovieCategory.ALL -> "All"
                                MovieCategory.FAVORITES -> "Favorites"
                                MovieCategory.WATCHED -> "Watched"
                                MovieCategory.WATCHLIST -> "Watchlist"
                            }
                        )
                    }
                )
            }
        }

        when (val currentState = state) {
            is MovieListScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MovieListScreenState.Content -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = currentState.movies,
                        key = { it.id }
                    ) { movie ->
                        MovieCard(
                            movie = movie,
                            onMovieClick = onMovieClick,
                            onToggleFavorite = viewModel::onToggleFavorite,
                            onToggleWatchlist = viewModel::onToggleWatchlist
                        )
                    }
                }
            }

            is MovieListScreenState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No movies",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is MovieListScreenState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
