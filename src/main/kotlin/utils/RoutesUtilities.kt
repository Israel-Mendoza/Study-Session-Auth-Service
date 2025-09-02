package dev.artisra.utils

import dev.artisra.auth.models.LoginRequest
import dev.artisra.auth.models.LoginResponse
import dev.artisra.models.User
import dev.artisra.services.impl.UserServiceImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

private val logger = LoggerFactory.getLogger("RoutesUtilities")

suspend fun authenticateUserOrRespond(
    call: ApplicationCall,
    loginReq: LoginRequest,
    userServiceImpl: UserServiceImpl
): User? {
    return try {
        val user = userServiceImpl.authenticateUser(loginReq.username, loginReq.password)
        logger.info("User with username: ${loginReq.username} authenticated successfully.")
        user
    } catch (e: IllegalArgumentException) {
        val warnMessage = "Authentication failed for username: ${loginReq.username}. Reason: ${e.message}"
        logger.warn(warnMessage)
        val loginRes = LoginResponse(
            message = "Invalid username or password",
            token = null,
            expiration = null,
            timestamp = LocalDateTime.now().toString()
        )
        call.respond(HttpStatusCode.Unauthorized, loginRes)
        null
    }
}