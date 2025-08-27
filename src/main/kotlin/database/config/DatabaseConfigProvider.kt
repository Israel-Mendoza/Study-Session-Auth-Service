package dev.artisra.database.config

import dev.artisra.database.tables.UserTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(Application::class.java)

fun Application.configureDatabase() {

    val dbConfig = DatabaseConfig.fromEnvironment(this.environment)

    if (dbConfig != null) {
        val (dbUrl, driver, user, password) = dbConfig
        logger.info("Connecting to database at $dbUrl with user $user")
        Database.connect(dbUrl, driver = driver, user = user, password = password)
    } else {
        // Fallback to in-memory H2 database if configuration is missing
        logger.warn("Database configuration not found. Falling back to in-memory H2 database.")
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    }

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(UserTable)
    }
}