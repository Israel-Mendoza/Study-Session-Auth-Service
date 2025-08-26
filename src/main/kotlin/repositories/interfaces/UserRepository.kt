package dev.artisra.repositories.interfaces

import dev.artisra.models.UserRecord

interface UserRepository {
    suspend fun createUser(username: String, email: String, hashedPassword: String): Int
    suspend fun getUserByUsername(username: String): UserRecord?
    suspend fun getUserByEmail(email: String): UserRecord?
    suspend fun getUserById(id: Int): UserRecord?
    suspend fun updateUser(id: Int, username: String?, email: String?): Boolean
    suspend fun deleteUser(id: Int): Boolean
    suspend fun listUsers(): List<UserRecord>
}