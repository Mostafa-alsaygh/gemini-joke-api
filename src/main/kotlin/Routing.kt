package com.example

import com.example.model.ErrorResponse
import com.example.model.JokeResponse
import com.example.service.JokeService
import com.example.service.GeminiJokeService
import com.example.service.CachingJokeService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Configure Ktor application routes, plugins, and services.
 */
@JvmOverloads
fun Application.configureRouting(customJokeService: JokeService? = null) {
    
    // Install ContentNegotiation for automatic JSON serialization/deserialization on the server
    install(ServerContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    // Create a single HTTP Client instance for outbound calls to the Gemini API
    val httpClient = HttpClient(CIO) {
        install(ClientContentNegotiation) {
            gson()
        }
    }

    // Ensure the client is closed when the application stops to prevent resource leaks
    monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }

    // Load configuration & services
    val rawJokeService = customJokeService ?: run {
        val apiKey = System.getenv("GEMINI_API_KEY") ?: ""
        if (apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY environment variable is not configured. Outbound joke generation will fail.")
        }
        GeminiJokeService(httpClient, apiKey)
    }

    val jokeService = if (rawJokeService is CachingJokeService) {
        rawJokeService
    } else {
        CachingJokeService(rawJokeService, this)
    }

    routing {
        // Serve index.html and other static assets from the resource folder
        staticResources("/", "static", index = "index.html")

        // API Endpoint for generating jokes
        get("/thechancejoks/joke") {
            val word = call.request.queryParameters["word"]
            if (word.isNullOrBlank()) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse("Missing query parameter 'word'. Usage: /thechancejoks/joke?word=topic")
                )
                return@get
            }

            try {
                val joke = jokeService.generateIraqiJoke(word)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = JokeResponse(word = word, joke = joke)
                )
            } catch (e: Exception) {
                application.log.error("Error generating joke for topic '$word'", e)
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = ErrorResponse("Failed to generate joke: ${e.message ?: "Unknown error"}")
                )
            }
        }
    }
}