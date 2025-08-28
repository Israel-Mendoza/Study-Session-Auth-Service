package dev.artisra.repositories.impl

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class InMemoryUserRepositoryTest {

    private lateinit var repository: InMemoryUserRepository

    @BeforeTest
    fun setUp() {
        // Create a fresh instance for each test to ensure isolation
        repository = InMemoryUserRepository()
    }

    @Test
    fun createUser_shouldCreateAndReturnNewUser() = runTest {
        val id = repository.createUser("testuser", "test@example.com", "hashedpassword")
        val user = repository.getUserById(id)

        assertNotNull(user)
        assertEquals(1, id) // The first user created should have ID 1
        assertEquals("testuser", user.username)
        assertEquals("test@example.com", user.email)
        assertEquals("hashedpassword", user.passwordHash)
    }

    @Test
    fun getUserByUsername_shouldReturnUserIfExists() = runTest {
        // Create a user first
        repository.createUser("testuser", "test@example.com", "hashedpassword")

        val user = repository.getUserByUsername("testuser")
        assertNotNull(user)
        assertEquals("testuser", user.username)
    }

    @Test
    fun getUserByUsername_shouldReturnNullIfNotExists() = runTest {
        val user = repository.getUserByUsername("nonexistentuser")
        assertNull(user)
    }

    @Test
    fun getUserByEmail_shouldReturnUserIfExists() = runTest {
        // Create a user first
        repository.createUser("testuser", "test@example.com", "hashedpassword")

        val user = repository.getUserByEmail("test@example.com")
        assertNotNull(user)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun getUserByEmail_shouldReturnNullIfNotExists() = runTest {
        val user = repository.getUserByEmail("nonexistent@example.com")
        assertNull(user)
    }

    @Test
    fun getUserById_shouldReturnUserIfExists() = runTest {
        // Create a user and get its ID
        val id = repository.createUser("testuser", "test@example.com", "hashedpassword")

        val user = repository.getUserById(id)
        assertNotNull(user)
        assertEquals(id, user.id)
    }

    @Test
    fun getUserById_shouldReturnNullIfNotExists() = runTest {
        val user = repository.getUserById(999)
        assertNull(user)
    }

    @Test
    fun updateUser_shouldUpdateUsernameAndEmail() = runTest {
        val id = repository.createUser("olduser", "old@example.com", "hashedpassword")
        val isUpdated = repository.updateUser(id, "newuser", "new@example.com")
        assertTrue(isUpdated)

        val updatedUser = repository.getUserById(id)
        assertNotNull(updatedUser)
        assertEquals("newuser", updatedUser.username)
        assertEquals("new@example.com", updatedUser.email)
    }

    @Test
    fun updateUser_shouldUpdateOnlyUsername() = runTest {
        val id = repository.createUser("olduser", "old@example.com", "hashedpassword")
        val isUpdated = repository.updateUser(id, "newuser", null)
        assertTrue(isUpdated)

        val updatedUser = repository.getUserById(id)
        assertNotNull(updatedUser)
        assertEquals("newuser", updatedUser.username)
        assertEquals("old@example.com", updatedUser.email) // Email should remain unchanged
    }

    @Test
    fun updateUser_shouldReturnFalseForNonExistentUser() = runTest {
        val isUpdated = repository.updateUser(999, "newuser", "new@example.com")
        assertFalse(isUpdated)
    }

    @Test
    fun deleteUser_shouldDeleteUserSuccessfully() = runTest {
        val id = repository.createUser("userToDelete", "delete@example.com", "password")
        val isDeleted = repository.deleteUser(id)

        assertTrue(isDeleted)
        assertNull(repository.getUserById(id)) // The user should no longer exist
    }

    @Test
    fun deleteUser_shouldReturnFalseForNonExistentUser() = runTest {
        val isDeleted = repository.deleteUser(999)
        assertFalse(isDeleted)
    }

    @Test
    fun listUsers_shouldReturnAllUsers() = runTest {
        repository.createUser("user1", "user1@example.com", "pass1")
        repository.createUser("user2", "user2@example.com", "pass2")

        val users = repository.listUsers()
        assertEquals(2, users.size)
        assertTrue(users.any { it.username == "user1" })
        assertTrue(users.any { it.username == "user2" })
    }
}
