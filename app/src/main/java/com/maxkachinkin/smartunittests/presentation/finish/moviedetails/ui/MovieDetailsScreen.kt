package com.maxkachinkin.smartunittests.presentation.finish.moviedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.maxkachinkin.smartunittests.common.domain.model.Movie
import com.maxkachinkin.smartunittests.presentation.finish.moviedetails.MovieDetailsScreenState
import com.maxkachinkin.smartunittests.presentation.finish.moviedetails.MovieDetailsViewModel

@Composable
fun MovieDetailsScreenContent(
    viewModel: MovieDetailsViewModel
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is MovieDetailsScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is MovieDetailsScreenState.Content -> {
            MovieDetailsContent(
                movie = currentState.movie,
                onToggleFavorite = { viewModel.onToggleFavorite(currentState.movie.isFavorite) },
                onMarkAsWatched = { viewModel.onMarkAsWatched(currentState.movie.isWatched) },
                onToggleWatchlist = { viewModel.onToggleWatchlist(currentState.movie.isInWatchlist) }
            )
        }

        is MovieDetailsScreenState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movie: Movie,
    onToggleFavorite: () -> Unit,
    onMarkAsWatched: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${movie.year} | ${movie.genreNames.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Rating: ${"%.1f".format(movie.rating)} / 10",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = onToggleFavorite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null
                )
                Text(if (movie.isFavorite) "Unfavorite" else "Favorite")
            }

            FilledTonalButton(
                onClick = onMarkAsWatched,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (movie.isWatched) Icons.Filled.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = null
                )
                Text(if (movie.isWatched) "Watched" else "Mark watched")
            }

            FilledTonalButton(
                onClick = onToggleWatchlist,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (movie.isInWatchlist) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null
                )
                Text(if (movie.isInWatchlist) "Listed" else "Watchlist")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = movie.overview,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
