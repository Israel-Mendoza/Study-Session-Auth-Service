package dev.artisra.services.interfaces

import dev.artisra.auth.models.RegistrationRequest
import dev.artisra.models.User
import dev.artisra.models.UserRecord

interface UserService {
    suspend fun registerUser(userRequest: RegistrationRequest): UserRecord?
    suspend fun getUserByUsername(username: String): UserRecord?
    suspend fun getUserByEmail(email: String): UserRecord?
    suspend fun getUserById(id: Int): UserRecord?
    suspend fun authenticateUser(username: String, password: String): User
    suspend fun updateUser(userId: Int, username: String?, email: String?): Boolean
    suspend fun deleteUser(userId: Int): Boolean
    suspend fun listUsers(): List<UserRecord>
}