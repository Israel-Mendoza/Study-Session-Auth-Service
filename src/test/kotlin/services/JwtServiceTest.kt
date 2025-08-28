package dev.artisra.services

import dev.artisra.models.User
import dev.artisra.services.impl.JwtServiceImpl
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JwtServiceTest {
    private lateinit var jwtServiceImpl: JwtServiceImpl
    private val audience = "testAudience"
    private val issuer = "testIssuer"
    private val secret = "testSecret"
    private val expirationMs = 1000L

    @BeforeTest
    fun setUp() {
        jwtServiceImpl = JwtServiceImpl(audience, issuer, secret, expirationMs)
    }

    @Test
    fun generateToken_shouldReturnValidTokenPayload() {
        val user = User(id = 1, username = "testuser", passwordHash = "hash")
        val tokenPayload = jwtServiceImpl.generateToken(user)
        assertTrue(tokenPayload.token.isNotBlank())
        assertTrue(tokenPayload.expiration > System.currentTimeMillis())
    }

    @Test
    fun verifyToken_shouldReturnUserForValidToken() {
        val user = User(id = 2, username = "validuser", passwordHash = "hash")
        val token = jwtServiceImpl.generateToken(user).token
        val result = jwtServiceImpl.verifyToken(token)
        assertNotNull(result)
        assertEquals(user.id, result?.id)
        assertEquals(user.username, result?.username)
    }

    @Test
    fun verifyToken_shouldReturnNullForInvalidToken() {
        val invalidToken = "invalid.token.value"
        val result = jwtServiceImpl.verifyToken(invalidToken)
        assertNull(result)
    }

    @Test
    fun verifyToken_shouldReturnNullForExpiredToken() {
        val shortLivedService = JwtServiceImpl(audience, issuer, secret, 1L)
        val user = User(id = 3, username = "expireduser", passwordHash = "hash")
        val token = shortLivedService.generateToken(user).token
        Thread.sleep(10)
        val result = shortLivedService.verifyToken(token)
        assertNull(result)
    }

    @Test
    fun verifyToken_shouldReturnNullForIncorrectSecret() {
        // Generate a token with a different secret
        val differentSecretService = JwtServiceImpl(audience, issuer, "incorrectSecret", expirationMs)
        val user = User(id = 4, username = "wrongsecretuser", passwordHash = "hash")
        val token = differentSecretService.generateToken(user).token

        // Try to verify the token with the original service
        val result = jwtServiceImpl.verifyToken(token)
        assertNull(result)
    }
}
