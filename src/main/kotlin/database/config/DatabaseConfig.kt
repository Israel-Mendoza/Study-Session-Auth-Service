package dev.artisra.database.config

import io.ktor.server.application.ApplicationEnvironment

data class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
) {
    companion object {
        fun fromEnvironment(env: ApplicationEnvironment): DatabaseConfig? {
            val config = env.config
            val url = config.propertyOrNull("database.url")?.getString()
            val driver = config.propertyOrNull("database.driver")?.getString()
            val user = config.propertyOrNull("database.user")?.getString()
            val password = config.propertyOrNull("database.password")?.getString()

            return if (url != null && driver != null && user != null && password != null) {
                DatabaseConfig(url, driver, user, password)
            } else {
                null
            }
        }
    }
}