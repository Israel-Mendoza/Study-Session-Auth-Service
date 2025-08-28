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
        logger.info("Creating new user with username: \"$username\" and email: \"$email\"")
        val userId = UserTable.insert {
            it[UserTable.username] = username
            it[UserTable.email] = email
            it[UserTable.passwordHash] = hashedPassword
        }[UserTable.id]
        return@transaction userId
    }

    override suspend fun getUserByUsername(username: String): UserRecord? = transaction {
        val result = UserTable.select(UserTable.id, UserTable.username, UserTable.email, UserTable.passwordHash)
            .where { UserTable.username eq username }.map {
                UserMapper.fromResultRowToRecord(it)
            }.singleOrNull()

        logger.info("getUserByUsername result for '$username': $result")
        return@transaction result
    }

    override suspend fun getUserByEmail(email: String): UserRecord? = transaction {
        return@transaction UserTable.select(UserTable.id, UserTable.username, UserTable.email, UserTable.passwordHash)
            .where { UserTable.email eq email }.map {
                UserMapper.fromResultRowToRecord(it)
            }.singleOrNull()
    }

    override suspend fun getUserById(id: Int): UserRecord? = transaction {
        val result = UserTable.select(UserTable.id, UserTable.username, UserTable.email, UserTable.passwordHash)
            .where { UserTable.id eq id }.map {
                UserMapper.fromResultRowToRecord(it)
            }.singleOrNull()
        logger.info("getUserById result for ID '$id': $result")
        return@transaction result
    }

    override suspend fun updateUser(id: Int, username: String?, email: String?): Boolean = transaction {
        val updatedRecords = UserTable.update({ UserTable.id eq id }) {
            if (username != null) {
                logger.info("Updating username for user ID $id to '$username'")
                it[UserTable.username] = username
            }
            if (email != null) {
                logger.info("Updating email for user ID $id to '$email'")
                it[UserTable.email] = email
            }
        }
        return@transaction updatedRecords > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = transaction {
        val deletedRecords = UserTable.deleteWhere { UserTable.id eq id }
        logger.info("Deleted $deletedRecords record(s) for user ID $id")
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