package com.maxkachinkin.smartunittests.testutil

import com.maxkachinkin.smartunittests.common.data.local.MovieUserState
import com.maxkachinkin.smartunittests.common.data.local.UserMovieStateDataSource

class FakeUserMovieStateDataSource : UserMovieStateDataSource {

    private val states = mutableMapOf<Int, MovieUserState>()

    fun setState(movieId: Int, state: MovieUserState) {
        states[movieId] = state
    }

    override suspend fun getState(movieId: Int): MovieUserState {
        return states[movieId] ?: MovieUserState()
    }

    override suspend fun getAllStates(): Map<Int, MovieUserState> {
        return states.toMap()
    }

    override suspend fun setFavorite(movieId: Int, isFavorite: Boolean) {
        val current = states[movieId] ?: MovieUserState()
        states[movieId] = current.copy(isFavorite = isFavorite)
    }

    override suspend fun setWatched(movieId: Int, isWatched: Boolean) {
        val current = states[movieId] ?: MovieUserState()
        states[movieId] = current.copy(isWatched = isWatched)
    }

    override suspend fun setInWatchlist(movieId: Int, isInWatchlist: Boolean) {
        val current = states[movieId] ?: MovieUserState()
        states[movieId] = current.copy(isInWatchlist = isInWatchlist)
    }
}
