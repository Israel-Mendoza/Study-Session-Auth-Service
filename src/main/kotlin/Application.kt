package dev.artisra

import dev.artisra.database.config.configureDatabase
import dev.artisra.plugins.configureRouting
import dev.artisra.plugins.configureSerialization
import dev.artisra.services.impl.JwtServiceImpl
import dev.artisra.services.impl.UserService
import dev.artisra.plugins.configureSecurity
import dev.artisra.repositories.impl.DbUserRepository
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {

    // Initialize JwtService with configuration values
    val jwtServiceImpl = JwtServiceImpl(
        audience = environment.config.property("jwt.audience").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        secret = environment.config.property("jwt.secret").getString(),
        expirationMs = environment.config.property("jwt.expirationMs").getString().toLong()
    )

    // Initialize UserService with DbUserRepository
    val userRepository = DbUserRepository()
    val userServiceImpl = UserService(userRepository)

    // Call the new configuration function
    configureDatabase()
    configureSerialization()
    configureSecurity()
    configureRouting(userServiceImpl, jwtServiceImpl)
}