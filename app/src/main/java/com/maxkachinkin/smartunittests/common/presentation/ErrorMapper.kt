package com.maxkachinkin.smartunittests.common.presentation

import com.maxkachinkin.smartunittests.common.data.remote.HttpApiException
import java.net.UnknownHostException

object ErrorMapper {

    fun toUserMessage(e: Exception): String {
        return when (e) {
            is HttpApiException -> e.toUserMessage()
            is UnknownHostException -> "No internet connection. Please check your network."
            else -> e.message ?: "Unknown error"
        }
    }
}
