package com.example.service

/**
 * Service interface for generating jokes.
 */
interface JokeService {
    /**
     * Generates a funny Iraqi joke about the given [word].
     *
     * @param word The topic or theme of the joke.
     * @return The generated joke text.
     */
    suspend fun generateIraqiJoke(word: String): String
}
