package dev.artisra.routes

import dev.artisra.services.impl.UserServiceImpl
import dev.artisra.services.interfaces.JwtService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import io.mockk.coEvery
import dev.artisra.auth.models.*
import dev.artisra.models.UserRecord
import java.time.LocalDateTime
import java.time.ZoneOffset
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.auth.basic
import org.junit.jupiter.api.Disabled

class UserRoutesTest {
    // Mock the dependencies
    private val mockUserService = mockk<UserServiceImpl>(relaxed = true)
    private val mockJwtService = mockk<JwtService>(relaxed = true)

    // Create dummy data
    private val dummyUser = UserResponse(id = 1, username = "testuser")
    private val dummyToken = Token("dummy_token_string", LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli())


    fun `test user registration success`() = testApplication {
        // Correct placement: Both install and routing must be inside the application block
        application {
            // Install the ContentNegotiation plugin for JSON serialization
            install(ContentNegotiation) {
                json()
            }
            install(io.ktor.server.auth.Authentication) {
                // Add a dummy provider if needed, e.g.:
                basic("dummy") {
                    validate { credentials -> null } // Always fails, or provide a mock
                }
            }
            // Define the routing for the test application
            routing {
                route("/api/v1") {
                    userRoutes(mockUserService, mockJwtService)
                }
            }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        // Arrange
        val registrationRequest = RegistrationRequest(username = "testuser", email = "test@example.com", password = "password123")
        val successfulResponse = RegistrationResponse("User created successfully", "", dummyUser)
        val dummyUserRecord = UserRecord(1, "testuser", "test@example.com", "hashed_password")
        coEvery { mockUserService.registerUser(any()) } returns dummyUserRecord

        // Act & Assert
        val response = client.post("/api/v1/register") {
            contentType(ContentType.Application.Json)
            setBody(registrationRequest)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertNotNull(response.bodyAsText())
        val registrationResponse = response.bodyAsText().let {
            kotlinx.serialization.json.Json.decodeFromString<RegistrationResponse>(it)
        }
        assertEquals(successfulResponse.message, registrationResponse.message)
    }

    fun `test user registration failure on existing user`() = testApplication {
        // Correct placement: Both install and routing must be inside the application block
        application {
            // Install the ContentNegotiation plugin
            install(ContentNegotiation) {
                json()
            }
            // Define the routing for the test application
            routing {
                route("/api/v1") {
                    userRoutes(mockUserService, mockJwtService)
                }
            }
        }

        // Arrange
        val registrationRequest = RegistrationRequest(username = "existinguser", email = "existing@example.com", password = "password123")
        coEvery { mockUserService.registerUser(any()) } returns null

        // Act & Assert
        val response = client.post("/api/v1/register") {
            contentType(ContentType.Application.Json)
            setBody(registrationRequest)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertNotNull(response.bodyAsText())
        val registrationResponse = response.bodyAsText().let {
            kotlinx.serialization.json.Json.decodeFromString<RegistrationResponse>(it)
        }
        assertEquals("User or email exists already", registrationResponse.message)
    }
}