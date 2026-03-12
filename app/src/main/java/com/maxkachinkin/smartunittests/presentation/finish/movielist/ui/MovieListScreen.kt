package com.maxkachinkin.smartunittests.presentation.finish.movielist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxkachinkin.smartunittests.common.domain.model.MovieCategory
import com.maxkachinkin.smartunittests.common.domain.model.MovieSortOption
import com.maxkachinkin.smartunittests.presentation.finish.movielist.EmptyReason
import com.maxkachinkin.smartunittests.presentation.finish.movielist.MovieListScreenState
import com.maxkachinkin.smartunittests.presentation.finish.movielist.MovieListViewModel

@Composable
fun MovieListScreenContent(
    viewModel: MovieListViewModel,
    onMovieClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        val selectedCategory = when (val s = state) {
            is MovieListScreenState.Content -> s.selectedCategory
            is MovieListScreenState.Empty -> s.selectedCategory
            else -> MovieCategory.ALL
        }

        CategoryChips(
            selectedCategory = selectedCategory,
            onCategorySelected = viewModel::onCategorySelected
        )

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
                if (currentState.isUsingCachedData) {
                    Text(
                        text = "Showing cached data. Pull to refresh.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                SortRow(
                    selectedSortOption = currentState.selectedSortOption,
                    onSortOptionSelected = viewModel::onSortOptionSelected
                )

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
                        text = when (currentState.reason) {
                            EmptyReason.NO_MOVIES -> "No movies available"
                            EmptyReason.NO_FAVORITES -> "No favorite movies yet"
                            EmptyReason.NO_WATCHED -> "No watched movies yet"
                            EmptyReason.NO_WATCHLIST -> "No movies in watchlist yet"
                        },
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = viewModel::onRefresh) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChips(
    selectedCategory: MovieCategory,
    onCategorySelected: (MovieCategory) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(MovieCategory.entries.toList()) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
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
}

@Composable
private fun SortRow(
    selectedSortOption: MovieSortOption,
    onSortOptionSelected: (MovieSortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MovieSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when (option) {
                                MovieSortOption.DEFAULT -> "Default"
                                MovieSortOption.BY_RATING -> "By Rating"
                                MovieSortOption.BY_YEAR -> "By Year"
                            }
                        )
                    },
                    onClick = {
                        onSortOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
