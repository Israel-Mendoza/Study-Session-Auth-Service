package dev.artisra.services.impl

import dev.artisra.auth.models.RegistrationRequest
import dev.artisra.models.User
import dev.artisra.models.UserRecord
import dev.artisra.repositories.interfaces.UserRepository
import dev.artisra.services.interfaces.UserService
import org.mindrot.jbcrypt.BCrypt

class UserService(private val userRepository: UserRepository) : UserService {
    override suspend fun registerUser(userRequest: RegistrationRequest): UserRecord? {
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

    override suspend fun getUserByUsername(username: String) = userRepository.getUserByUsername(username)

    override suspend fun getUserByEmail(email: String) = userRepository.getUserByEmail(email)

    override suspend fun getUserById(id: Int) = userRepository.getUserById(id)

    override suspend fun authenticateUser(username: String, password: String): User {
        val userRecord = userRepository.getUserByUsername(username)
            ?: throw IllegalArgumentException("User not found")
        if (!BCrypt.checkpw(password, userRecord.passwordHash)) {
            throw IllegalArgumentException("Invalid password")
        }
        return User(id = userRecord.id, username = userRecord.username, passwordHash = userRecord.passwordHash)
    }

    override suspend fun updateUser(userId: Int, username: String?, email: String?) = userRepository.updateUser(userId, username, email)

    override suspend fun deleteUser(userId: Int) = userRepository.deleteUser(userId)

    override suspend fun listUsers() = userRepository.listUsers()
}