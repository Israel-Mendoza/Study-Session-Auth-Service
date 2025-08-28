package dev.artisra.plugins

import dev.artisra.auth.models.RegistrationRequest
import dev.artisra.auth.models.LoginRequest
import dev.artisra.auth.models.RegistrationResponse
import dev.artisra.auth.models.UserResponse
import dev.artisra.services.impl.JwtServiceImpl
import dev.artisra.services.impl.UserServiceImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory // Import the logger factory
import java.time.LocalDateTime

// Define a logger for this file
private val logger = LoggerFactory.getLogger(Application::class.java)

fun Application.configureRouting(
    userService: UserServiceImpl,
    jwtServiceImpl: JwtServiceImpl,
) {
    routing {
        route("/api/v1") {
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
                val user = userService.authenticateUser(loginReq.username, loginReq.password)
                val token = jwtServiceImpl.generateToken(user)
                logger.debug("Generated token for user ${user.username}")
                call.respond(HttpStatusCode.OK, token)
            }

            authenticate {
                get("/protected") {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal!!.getClaim("username", String::class)
                    call.respond(HttpStatusCode.OK, "Welcome, $username!")
                }
            }
        }
    }
}
