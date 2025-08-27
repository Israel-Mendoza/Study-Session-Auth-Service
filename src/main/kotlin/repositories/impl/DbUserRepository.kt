package dev.artisra.repositories.impl

import dev.artisra.database.tables.UserTable
import dev.artisra.mappers.UserMapper
import dev.artisra.models.UserRecord
import dev.artisra.repositories.interfaces.UserRepository
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory

class DbUserRepository : UserRepository {
    override suspend fun createUser(
        username: String,
        email: String,
        hashedPassword: String,
    ): Int = transaction {
        logger.warn("Username: $username, Email: $email, HashedPassword: $hashedPassword")
        val userId = UserTable.insert {
            it[UserTable.username] = username
            it[UserTable.email] = email
            it[UserTable.passwordHash] = hashedPassword
        }[UserTable.id]
        return@transaction userId
    }

    override suspend fun getUserByUsername(username: String): UserRecord? = transaction {
        logger.warn("USING USERNAME: $username")
        val result = UserTable.select(UserTable.id, UserTable.username, UserTable.email, UserTable.passwordHash)
            .where { UserTable.username eq username }.map {
                UserMapper.fromResultRowToRecord(it)
            }.singleOrNull()

        logger.warn("getUserByUsername result for '$username': $result")
        return@transaction result
    }

    override suspend fun getUserByEmail(email: String): UserRecord? = transaction {
        return@transaction UserTable.select(UserTable.id, UserTable.username, UserTable.email, UserTable.passwordHash)
            .where { UserTable.email eq email }.map {
                UserMapper.fromResultRowToRecord(it)
            }.singleOrNull()
    }

    override suspend fun getUserById(id: Int): UserRecord? = transaction {
        logger.warn("USING ID: $id")
        val result = UserTable.select(UserTable.id, UserTable.username, UserTable.email, UserTable.passwordHash)
            .where { UserTable.id eq id }.map {
                UserMapper.fromResultRowToRecord(it)
            }.singleOrNull()
        logger.warn("getUserById result for ID '$id': $result")
        return@transaction result
    }

    override suspend fun updateUser(id: Int, username: String?, email: String?): Boolean = transaction {
        val updatedRecords = UserTable.update({ UserTable.id eq id }) {
            if (username != null) {
                it[UserTable.username] = username
            }
            if (email != null) {
                it[UserTable.email] = email
            }
        }
        return@transaction updatedRecords > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = transaction {
        val deletedRecords = UserTable.deleteWhere { UserTable.id eq id }
        return@transaction deletedRecords > 0
    }

    override suspend fun listUsers(): List<UserRecord> = transaction {
        return@transaction UserTable.select(UserTable.id, UserTable.username, UserTable.email, UserTable.passwordHash).map {
            UserMapper.fromResultRowToRecord(it)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DbUserRepository::class.java)
    }
}