package dev.artisra.utils.mocks

import dev.artisra.models.UserRecord
import dev.artisra.repositories.interfaces.UserRepository

class MockUserRepository : UserRepository {
    // In-memory data store for testing
    private val users = mutableListOf<UserRecord>()
    private var nextId = 1

    override suspend fun getUserByUsername(username: String): UserRecord? {
        return users.find { it.username == username }
    }

    override suspend fun getUserByEmail(email: String): UserRecord? {
        return users.find { it.email == email }
    }

    override suspend fun getUserById(id: Int): UserRecord? {
        return users.find { it.id == id }
    }

    override suspend fun createUser(username: String, email: String, hashedPassword: String): Int {
        val newId = nextId++
        val newUser = UserRecord(newId, username, email, hashedPassword)
        users.add(newUser)
        return newId
    }

    override suspend fun updateUser(id: Int, username: String?, email: String?): Boolean {
        val userToUpdate = users.find { it.id == id } ?: return false
        val updatedUser = userToUpdate.copy(
            username = username ?: userToUpdate.username,
            email = email ?: userToUpdate.email
        )
        users[users.indexOf(userToUpdate)] = updatedUser
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        val userToRemove = users.find { it.id == id } ?: return false
        return users.remove(userToRemove)
    }

    override suspend fun listUsers(): List<UserRecord> {
        return users.toList()
    }
}