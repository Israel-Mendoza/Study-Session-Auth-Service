package dev.artisra.repositories.impl

import dev.artisra.models.UserRecord
import dev.artisra.repositories.interfaces.UserRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class InMemoryUserRepository : UserRepository {

    private val users = ConcurrentHashMap<Int, UserRecord>()

    private val nextId = AtomicInteger(1)


    override suspend fun createUser(
        username: String,
        email: String,
        hashedPassword: String,
    ): Int {
        val id = nextId.getAndIncrement()
        val user = UserRecord(
            id = id,
            username = username,
            email = email,
            passwordHash = hashedPassword
        )
        users[id] = user
        return id
    }

    override suspend fun getUserByUsername(username: String) = users.values.find { it.username == username }

    override suspend fun getUserByEmail(email: String)= users.values.find { it.email == email }

    override suspend fun getUserById(id: Int) = users.values.find { it.id == id }

    override suspend fun updateUser(id: Int, username: String?, email: String?): Boolean {
        // Check if user exists
        val user = getUserById(id) ?: return false

        // Update user details
        val updatedUser = user.copy(
            username = username ?: user.username,
            email = email ?: user.email
        )
        users[id] = updatedUser
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        return users.remove(id) != null
    }

    override suspend fun listUsers(): List<UserRecord> {
        return users.values.toList()
    }
}