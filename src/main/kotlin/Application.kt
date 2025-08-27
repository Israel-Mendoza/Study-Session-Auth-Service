package dev.artisra

import dev.artisra.database.tables.UserTable
import dev.artisra.plugins.configureRouting
import dev.artisra.plugins.configureSerialization
import dev.artisra.repositories.impl.InMemoryUserRepository
import dev.artisra.services.JwtService
import dev.artisra.services.UserService
import dev.artisra.plugins.configureSecurity
import dev.artisra.repositories.impl.DbUserRepository
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.Schema
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(UserTable)
    }

//    val userRepository = InMemoryUserRepository()
    val userRepository = DbUserRepository()
    val userService = UserService(userRepository)
    val jwtService = JwtService(
        audience = environment.config.property("jwt.audience").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        secret = environment.config.property("jwt.secret").getString(),
    )

    // Call the new configuration function
    configureSerialization()
    configureSecurity()
    configureRouting(userService, jwtService)
}