// In file: UserRoutes.kt
package dev.artisra.routes

import dev.artisra.auth.models.RegistrationRequest
import dev.artisra.auth.models.LoginRequest
import dev.artisra.auth.models.LoginResponse
import dev.artisra.auth.models.RegistrationResponse
import dev.artisra.auth.models.UserResponse
import dev.artisra.services.impl.UserService
import dev.artisra.services.interfaces.JwtService
import dev.artisra.utils.authenticateUserOrRespond
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

private val logger = LoggerFactory.getLogger("UserRoutes")

fun Route.userRoutes(userService: UserService, jwtService: JwtService) {
    post("/register") {
        // Handle user registration
        val authRequest = call.receive<RegistrationRequest>()
        val userRecord = userService.registerUser(authRequest)
        val localDateTimeStr = LocalDateTime.now().toString()
        if (userRecord == null) {
            val warnMessage = "Registration failed for username: ${authRequest.username}, email: ${authRequest.email}"
            logger.warn(warnMessage)
            val failureResponse = RegistrationResponse("User or email exists already", localDateTimeStr, null)
            call.respond(HttpStatusCode.BadRequest, failureResponse)
        } else {
            logger.info("User with username: ${authRequest.username} and email: ${authRequest.email} registered successfully.")
            val successfulRegistration = RegistrationResponse(
                "User created successfully", localDateTimeStr, UserResponse(
                    id = userRecord.id,
                    username = userRecord.username,
                )
            )
            call.respond(HttpStatusCode.Created, successfulRegistration)
        }
    }

    post("/login") {
        val loginReq = call.receive<LoginRequest>()
        val user = authenticateUserOrRespond(call, loginReq, userService) ?: return@post

        val token = jwtService.generateToken(user)
        val loginRes = LoginResponse(
            message = "Login successful",
            token = token.token,
            expiration = token.expiration,
            timestamp = LocalDateTime.now().toString()
        )
        logger.debug("Generated token for user ${user.username}")
        call.respond(HttpStatusCode.OK, loginRes)
    }

    authenticate {
        get("/protected") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.getClaim("username", String::class)
            call.respond(HttpStatusCode.OK, "Welcome, $username!")
        }
    }
}