package com.example.service

import com.google.gson.annotations.SerializedName
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.UUID

// Data models representing Gemini API payload structure
private data class GeminiPart(
    val text: String
)

private data class GeminiContent(
    val parts: List<GeminiPart>
)

private data class GeminiGenerationConfig(
    val temperature: Double
)

private data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("generationConfig")
    val generationConfig: GeminiGenerationConfig
)

private data class GeminiCandidate(
    val content: GeminiContent?
)

private data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

/**
 * Implementation of [JokeService] that communicates with Google Gemini API.
 */
class GeminiJokeService(
    private val httpClient: HttpClient,
    private val apiKey: String
) : JokeService {

    override suspend fun generateIraqiJoke(word: String): String {
        require(apiKey.isNotBlank()) {
            "GEMINI_API_KEY environment variable is not configured. Please set it in your settings."
        }

        val randomId = UUID.randomUUID().toString()
        
        // Construct the prompt with clear instructions for the model
        val prompt = """
            Generate a funny, clean joke in the Iraqi Arabic dialect (using Iraqi slang and cultural references, written in Arabic script) about the topic: '$word'.
            To ensure variety and that it is different every time, use this unique random seed: '$randomId'.
            Do not repeat jokes. Make it extremely funny, creative, and lighthearted. Do not include any racism, hate speech, or offensive content.
            Respond only with the joke itself, no introductory text, no quotes, no markdown formatting.
        """.trimIndent()

        val requestBody = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt)
                    )
                )
            ),
            generationConfig = GeminiGenerationConfig(temperature = 1.0)//todo temperature controls the randomness/creativity of the output (e.g., 1.0 makes the responses more creative and varied).
        )

        val response: HttpResponse = httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header("x-goog-api-key", apiKey)
            setBody(requestBody)
        }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            throw RuntimeException("Gemini API error (Status ${response.status}): ${response.status.description}. Details: $errorBody")
        }

        val geminiResponse = response.body<GeminiResponse>()
        val jokeText = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw RuntimeException("No text returned from Gemini API response.")

        return jokeText.trim()
    }
}
