package com.maxkachinkin.smartunittests.common.data.remote

class HttpApiException(
    val statusCode: Int,
    val rawBody: String
) : Exception("HTTP $statusCode") {

    fun toUserMessage(): String {
        return when (statusCode) {
            401 -> "Authentication failed. Please check your API key."
            403 -> "Access denied. You don't have permission to access this resource."
            404 -> "Content not found. It may have been removed."
            408 -> "Request timed out. Please try again."
            429 -> "Too many requests. Please wait a moment and try again."
            in 400..499 -> "Request error. Please try again later."
            in 500..599 -> "Server error. The service is temporarily unavailable."
            else -> "Unexpected error (code $statusCode). Please try again."
        }
    }
}
