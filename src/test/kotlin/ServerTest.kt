package com.example

import com.example.service.JokeService
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.*

class ServerTest {

    @Test
    fun `test root endpoint returns index html`() = testApplication {
        // Load the default application configuration
        application {
            configureRouting()
        }
        
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test joke endpoint returns joke successfully`() = testApplication {
        val mockJokeService = object : JokeService {
            override suspend fun generateIraqiJoke(word: String): String {
                return "هذه نكتة عراقية مضحكة عن $word"
            }
        }
        
        application {
            configureRouting(mockJokeService)
        }
        
        val response = client.get("/thechancejoks/joke?word=سفر")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("سفر"), "Response should contain the keyword 'سفر'")
        assertTrue(body.contains("هذه نكتة عراقية مضحكة"), "Response should contain the mock joke content")
    }

    @Test
    fun `test joke endpoint returns bad request when word parameter is missing`() = testApplication {
        application {
            configureRouting()
        }
        
        val response = client.get("/thechancejoks/joke")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Missing query parameter"), "Response should state that the parameter is missing")
    }
}
