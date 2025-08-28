package dev.artisra.repositories.impl

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.artisra.database.tables.UserTable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class DbUserRepositoryTest {

    // Use an in-memory H2 database for testing
    private val h2Database by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
            driverClassName = "org.h2.Driver"
            username = "root"
            password = ""
            maximumPoolSize = 1
            isAutoCommit = true
            validate()
        }
        HikariDataSource(config)
    }

    private lateinit var repository: DbUserRepository

    @BeforeTest
    fun setUp() = runBlocking {
        // Connect to the database
        Database.connect(h2Database)
        // Drop and recreate the schema for each test to ensure isolation
        transaction {
            SchemaUtils.drop(UserTable)
            SchemaUtils.create(UserTable)
        }
        repository = DbUserRepository()
    }

    // We don't need to tear down the tables explicitly as they are in-memory and will be
    // cleared with each new test run

    @Test
    fun createUser_shouldCreateUserAndReturnId() = runTest {
        val id = repository.createUser("testuser", "test@example.com", "hashedpassword")
        val user = repository.getUserById(id)
        assertNotNull(user)
        assertEquals("testuser", user.username)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun getUserByUsername_shouldReturnUserIfItExists() = runTest {
        // First create a user in the database
        val id = repository.createUser("testuser", "test@example.com", "hashedpassword")

        val user = repository.getUserByUsername("testuser")
        assertNotNull(user)
        assertEquals(id, user.id)
        assertEquals("testuser", user.username)
    }

    @Test
    fun getUserByUsername_shouldReturnNullIfNotExists() = runTest {
        val user = repository.getUserByUsername("nonexistentuser")
        assertNull(user)
    }

    @Test
    fun getUserByEmail_shouldReturnUserIfItExists() = runTest {
        val id = repository.createUser("testuser", "test@example.com", "hashedpassword")

        val user = repository.getUserByEmail("test@example.com")
        assertNotNull(user)
        assertEquals(id, user.id)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun getUserByEmail_shouldReturnNullIfNotExists() = runTest {
        val user = repository.getUserByEmail("nonexistent@example.com")
        assertNull(user)
    }

    @Test
    fun getUserById_shouldReturnUserIfItExists() = runTest {
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
    fun updateUser_shouldReturnFalseForNonExistentUser() = runTest {
        val isUpdated = repository.updateUser(999, "newuser", "new@example.com")
        assertFalse(isUpdated)
    }

    @Test
    fun deleteUser_shouldDeleteUserSuccessfully() = runTest {
        val id = repository.createUser("userToDelete", "delete@example.com", "password")
        val isDeleted = repository.deleteUser(id)

        assertTrue(isDeleted)
        assertNull(repository.getUserById(id))
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
