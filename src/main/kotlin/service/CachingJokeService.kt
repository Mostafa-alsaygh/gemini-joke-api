package com.example.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import org.slf4j.LoggerFactory

/**
 * A decorating [JokeService] that adds in-memory caching and background generation.
 *
 * When a request for a word is made:
 * 1. If there is a cached joke for the word, it returns it immediately (no wait time).
 * 2. If the cache is empty, it generates the first joke synchronously (taking 1-3 seconds).
 *    Immediately after, it triggers a background coroutine task to generate 3 more jokes
 *    for that word and caches them for subsequent requests.
 */
class CachingJokeService(
    private val delegate: JokeService,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : JokeService {

    private val logger = LoggerFactory.getLogger(CachingJokeService::class.java)

    // Cache of words to their queue of pre-generated jokes
    private val cache = ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>()

    override suspend fun generateIraqiJoke(word: String): String {
        val key = word.trim().lowercase()
        val queue = cache[key]
        val cachedJoke = queue?.poll()

        if (cachedJoke != null) {
            logger.info("Cache hit for word '{}'. Returning cached joke immediately.", word)
            return cachedJoke
        }

        logger.info("Cache miss for word '{}'. Generating joke synchronously.", word)
        val joke = delegate.generateIraqiJoke(word)

        // Launch background task to pre-generate 3 more jokes
        coroutineScope.launch {
            logger.info("Background task started to generate 3 jokes for word '{}'.", word)
            val jokeQueue = cache.computeIfAbsent(key) { ConcurrentLinkedQueue() }
            repeat(3) { index ->
                try {
                    val extraJoke = delegate.generateIraqiJoke(word)
                    jokeQueue.offer(extraJoke)
                    logger.info("Generated and cached background joke #{} for word '{}'. Cache size: {}", index + 1, word, jokeQueue.size)
                } catch (e: Exception) {
                    logger.error("Failed to generate background joke #{} for word '{}'", index + 1, word, e)
                }
            }
        }

        return joke
    }

    /**
     * Helper to get current cache size for a word (useful for verification and testing).
     */
    fun getCacheSize(word: String): Int {
        val key = word.trim().lowercase()
        return cache[key]?.size ?: 0
    }
}
