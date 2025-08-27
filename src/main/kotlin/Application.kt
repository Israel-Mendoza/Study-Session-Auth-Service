package dev.artisra

import dev.artisra.database.config.configureDatabase
import dev.artisra.plugins.configureRouting
import dev.artisra.plugins.configureSerialization
import dev.artisra.services.JwtService
import dev.artisra.services.UserService
import dev.artisra.plugins.configureSecurity
import dev.artisra.repositories.impl.DbUserRepository
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {

    val userRepository = DbUserRepository()
    val userService = UserService(userRepository)
    val jwtService = JwtService(
        audience = environment.config.property("jwt.audience").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        secret = environment.config.property("jwt.secret").getString(),
    )

    // Call the new configuration function
    configureDatabase()
    configureSerialization()
    configureSecurity()
    configureRouting(userService, jwtService)
}