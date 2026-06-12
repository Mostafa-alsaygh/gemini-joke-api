package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID

fun Application.configureRouting() {
    routing {

        staticResources("/content", "mycontent")

        get("/") {
            call.respondText("Welcome to the Gemini Joke API! Use /thechancejoks/joke?word=yourword to get a funny joke.")
        }

        get("/thechancejoks/joke") {
            val word = call.request.queryParameters["word"]
            if (word.isNullOrBlank()) {
                call.respondText(
                    "{\"error\":\"Missing query parameter 'word'. Usage: /thechancejoks/joke?word=topic\"}",
                    ContentType.Application.Json.withCharset(Charsets.UTF_8),
                    HttpStatusCode.BadRequest
                )
                return@get
            }

            try {
                val joke = generateIraqiJoke(word)
                val escapedJoke = escapeJsonString(joke).removeSurrounding("\"")
                val responseJson = """{"word":"$word","joke":"$escapedJoke"}"""
                call.respondText(
                    responseJson,
                    ContentType.Application.Json.withCharset(Charsets.UTF_8),
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                val errorMsg = escapeJsonString(e.message ?: "Unknown error").removeSurrounding("\"")
                call.respondText(
                    """{"error":"Failed to generate joke: $errorMsg"}""",
                    ContentType.Application.Json.withCharset(Charsets.UTF_8),
                    HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
//AQ.Ab8RN6IUVJ-2A8_hLpYZFnbJ0XTw0_Y7nMMKiMG5bvb2d7ZhEA
private fun generateIraqiJoke(word: String): String {
    val apiKey = System.getenv("GEMINI_API_KEY") ?: "AQ.Ab8RN6IUVJ-2A8_hLpYZFnbJ0XTw0_Y7nMMKiMG5bvb2d7ZhEA"
    val randomId = UUID.randomUUID().toString()

    val prompt = """
        Generate a funny, clean joke in the Iraqi Arabic dialect (using Iraqi slang and cultural references, written in Arabic script) about the topic: '$word'.
        To ensure variety and that it is different every time, use this unique random seed: '$randomId'.
        Do not repeat jokes. Make it extremely funny, creative, and lighthearted. Do not include any racism, hate speech, or offensive content.
        Respond only with the joke itself, no introductory text, no quotes, no markdown formatting.
    """.trimIndent()

    val requestBody = """
        {
          "contents": [{
            "parts": [{
              "text": ${escapeJsonString(prompt)}
            }]
          }],
          "generationConfig": {
            "temperature": 1.0
          }
        }
    """.trimIndent()

    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=$apiKey")) //todo change it to flash 3.5
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() != 200) {
        throw RuntimeException("Gemini API error (Status ${response.statusCode()}): ${response.body()}")
    }

    return extractTextFromGeminiResponse(response.body())
}

private fun escapeJsonString(value: String): String {
    val builder = StringBuilder()
    builder.append("\"")
    for (char in value) {
        when (char) {
            '\\' -> builder.append("\\\\")
            '\"' -> builder.append("\\\"")
            '\n' -> builder.append("\\n")
            '\r' -> builder.append("\\r")
            '\t' -> builder.append("\\t")
            else -> {
                if (char.code < 0x20) {
                    builder.append(String.format("\\u%04x", char.code))
                } else {
                    builder.append(char)
                }
            }
        }
    }
    builder.append("\"")
    return builder.toString()
}

private fun extractTextFromGeminiResponse(response: String): String {
    val textIndex = response.indexOf("\"text\":")
    if (textIndex == -1) return "Could not generate a joke right now, try again!"
    val startQuoteIndex = response.indexOf("\"", textIndex + 7)
    if (startQuoteIndex == -1) return "Could not generate a joke right now, try again!"

    val sb = StringBuilder()
    var i = startQuoteIndex + 1
    while (i < response.length) {
        val char = response[i]
        if (char == '\\') {
            if (i + 1 < response.length) {
                val nextChar = response[i + 1]
                when (nextChar) {
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    '\"' -> sb.append('\"')
                    '\\' -> sb.append('\\')
                    else -> sb.append(nextChar)
                }
                i += 2
                continue
            }
        } else if (char == '\"') {
            break
        } else {
            sb.append(char)
        }
        i++
    }
    return sb.toString().trim()
}