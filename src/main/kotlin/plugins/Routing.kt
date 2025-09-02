package dev.artisra.plugins

import dev.artisra.routes.userRoutes
import dev.artisra.services.impl.UserService
import dev.artisra.services.interfaces.JwtService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory // Import the logger factory

// Define a logger for this file
private val logger = LoggerFactory.getLogger(Application::class.java)

fun Application.configureRouting(
    userService: UserService,
    jwtService: JwtService,
) {
    routing {
        route("/api/v1") {
            userRoutes(userService, jwtService)
        }
    }
}
