package dev.artisra.services

import dev.artisra.auth.models.RegistrationRequest
import dev.artisra.models.User
import dev.artisra.models.UserRecord
import dev.artisra.repositories.interfaces.UserRepository
import org.mindrot.jbcrypt.BCrypt

class UserService(private val userRepository: UserRepository) {
    suspend fun registerUser(userRequest: RegistrationRequest): UserRecord? {
        // Implement user registration logic here
        if (userRepository.getUserByUsername(userRequest.username) != null) {
            return null // Username already exists
        }
        if (userRepository.getUserByEmail(userRequest.email) != null) {
            return null // Email already exists
        }
        // Hash the password (implement hashing logic as needed)
        val passwordHash = BCrypt.hashpw(userRequest.password, BCrypt.gensalt())
        val newUserId = userRepository.createUser(userRequest.username, userRequest.email, passwordHash)
        return userRepository.getUserById(newUserId)
    }

    suspend fun getUserByUsername(username: String) = userRepository.getUserByUsername(username)

    suspend fun getUserByEmail(email: String) = userRepository.getUserByEmail(email)

    suspend fun getUserById(id: Int) = userRepository.getUserById(id)

    suspend fun authenticateUser(username: String, password: String): User {
        val userRecord = userRepository.getUserByUsername(username)
            ?: throw IllegalArgumentException("User not found")
        if (!BCrypt.checkpw(password, userRecord.passwordHash)) {
            throw IllegalArgumentException("Invalid password")
        }
        return User(id = userRecord.id, username = userRecord.username, passwordHash = userRecord.passwordHash)
    }

    suspend fun updateUser(userId: Int, username: String?, email: String?) = userRepository.updateUser(userId, username, email)

    suspend fun deleteUser(userId: Int) = userRepository.deleteUser(userId)

    suspend fun listUsers() = userRepository.listUsers()
}