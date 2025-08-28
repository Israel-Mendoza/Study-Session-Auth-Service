package dev.artisra.services

import dev.artisra.auth.models.RegistrationRequest
import dev.artisra.services.impl.UserServiceImpl

import dev.artisra.utils.mocks.MockUserRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith


class UserServiceTest {

    private lateinit var userService: UserServiceImpl
    private lateinit var mockUserRepository: MockUserRepository

    @BeforeTest
    fun setUp() {
        mockUserRepository = MockUserRepository()
        userService = UserServiceImpl(mockUserRepository)
    }

    @Test
    fun registerUser_shouldRegisterNewUserSuccessfully() = runTest {
        val request = RegistrationRequest("testuser", "password", "test@example.com")
        val userRecord = userService.registerUser(request)
        assertNotNull(userRecord)
        assertEquals(1, userRecord.id)
        assertEquals("testuser", userRecord.username)
        assertTrue(mockUserRepository.listUsers().size == 1)
    }

    @Test
    fun registerUser_shouldReturnNullForExistingUsername() = runTest {
        // Pre-register a user
        userService.registerUser(RegistrationRequest("testuser", "password", "test@example.com"))

        // Try to register with the same username
        val request = RegistrationRequest("testuser", "password2", "newemail@example.com")
        val userRecord = userService.registerUser(request)
        assertNull(userRecord)
        assertEquals(1, mockUserRepository.listUsers().size)
    }

    @Test
    fun registerUser_shouldReturnNullForExistingEmail() = runTest {
        // Pre-register a user
        userService.registerUser(RegistrationRequest("testuser", "password", "test@example.com"))

        // Try to register with the same email
        val request = RegistrationRequest("newuser", "password2", "test@example.com")
        val userRecord = userService.registerUser(request)
        assertNull(userRecord)
        assertEquals(1, mockUserRepository.listUsers().size)
    }

    @Test
    fun authenticateUser_shouldAuthenticateWithCorrectPassword() = runTest {
        // Pre-register a user
        val userRequest = RegistrationRequest("testuser", "password", "test@example.com")
        userService.registerUser(userRequest)

        val authenticatedUser = userService.authenticateUser(userRequest.username, userRequest.password)
        assertNotNull(authenticatedUser)
        assertEquals(userRequest.username, authenticatedUser.username)
    }

    @Test
    fun authenticateUser_shouldThrowExceptionForIncorrectPassword() = runTest {
        // Pre-register a user
        val userRequest = RegistrationRequest("testuser", "password", "test@example.com")
        userService.registerUser(userRequest)

        assertFailsWith<IllegalArgumentException> {
            userService.authenticateUser(userRequest.username, "wrongpassword")
        }
    }

    @Test
    fun updateUser_shouldUpdateUserSuccessfully() = runTest {
        // Pre-register a user
        val userRecord = userService.registerUser(RegistrationRequest("olduser", "old@example.com", "password"))
        assertNotNull(userRecord)

        val isUpdated = userService.updateUser(userRecord.id, "newuser", "new@example.com")
        assertTrue(isUpdated)

        val updatedUser = userService.getUserById(userRecord.id)
        assertNotNull(updatedUser)
        assertEquals("newuser", updatedUser.username)
        assertEquals("new@example.com", updatedUser.email)
    }

    @Test
    fun deleteUser_shouldDeleteUserSuccessfully() = runTest {
        // Pre-register a user
        val userRecord = userService.registerUser(RegistrationRequest("userToDelete", "delete@example.com", "password"))
        assertNotNull(userRecord)
        assertEquals(1, mockUserRepository.listUsers().size)

        val isDeleted = userService.deleteUser(userRecord.id)
        assertTrue(isDeleted)
        assertEquals(0, mockUserRepository.listUsers().size)
    }

    @Test
    fun listUsers_shouldReturnAllUsers() = runTest {
        // Register multiple users
        userService.registerUser(RegistrationRequest("user1", "pass1", "user1@example.com"))
        userService.registerUser(RegistrationRequest("user2", "pass2", "user2@example.com"))

        val users = userService.listUsers()
        assertEquals(2, users.size)
        assertEquals("user1", users[0].username)
        assertEquals("user2", users[1].username)
    }
}
