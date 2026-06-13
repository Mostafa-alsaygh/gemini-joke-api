package com.example.model

/**
 * Data class representing a successful joke response.
 */
data class JokeResponse(
    val word: String,
    val joke: String
)

/**
 * Data class representing an error response.
 */
data class ErrorResponse(
    val error: String
)
