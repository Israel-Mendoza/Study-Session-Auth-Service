package dev.artisra.database.tables

import org.jetbrains.exposed.v1.core.Table

const val MAX_USERNAME_LENGTH = 50
const val MAX_PASSWORD_LENGTH = 255

object UserTable : Table("users") {
    val id = integer("id").autoIncrement().uniqueIndex()
    val username = varchar("username", MAX_USERNAME_LENGTH).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", MAX_PASSWORD_LENGTH)
}