package com.maxkachinkin.smartunittests.common.data.local

data class MovieUserState(
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val isInWatchlist: Boolean = false
)

interface UserMovieStateDataSource {
    suspend fun getState(movieId: Int): MovieUserState
    suspend fun getAllStates(): Map<Int, MovieUserState>
    suspend fun setFavorite(movieId: Int, isFavorite: Boolean)
    suspend fun setWatched(movieId: Int, isWatched: Boolean)
    suspend fun setInWatchlist(movieId: Int, isInWatchlist: Boolean)
}

class UserMovieStateDataSourceImpl : UserMovieStateDataSource {

    private val states = mutableMapOf<Int, MovieUserState>()

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
