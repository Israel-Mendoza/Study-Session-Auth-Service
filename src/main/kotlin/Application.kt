package dev.artisra

import dev.artisra.plugins.configureRouting
import dev.artisra.plugins.configureSerialization
import dev.artisra.repositories.impl.InMemoryUserRepository
import dev.artisra.services.JwtService
import dev.artisra.services.UserService
import dev.artisra.plugins.configureSecurity
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    val userRepository = InMemoryUserRepository()
    val userService = UserService(userRepository)
    val jwtService = JwtService(
        jwtSecret = environment.config.property("jwt.secret").getString()
    )

    // Call the new configuration function
    configureSecurity()
    configureRouting(userService, jwtService)
}