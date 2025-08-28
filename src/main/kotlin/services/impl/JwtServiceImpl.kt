package dev.artisra.services.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.artisra.auth.models.LoginResponse
import dev.artisra.auth.models.Token
import dev.artisra.models.User
import dev.artisra.services.interfaces.JwtService
import java.util.Date

class JwtServiceImpl(
    private val audience: String,
    private val issuer: String,
    private val secret: String,
    private val expirationMs: Long,
) : JwtService {

    override fun generateToken(user: User): Token {
        val expiration = Date(System.currentTimeMillis() + expirationMs)

        val token = JWT.create().withAudience(audience).withIssuer(issuer)
            .withClaim("id", user.id) // Include user ID in the token claims
            .withClaim("username", user.username).withExpiresAt(expiration)
            .sign(Algorithm.HMAC256(secret))

        return Token(token = token, expiration = expiration.time)
    }

    override fun verifyToken(token: String): User? {
        val verifier = JWT.require(Algorithm.HMAC256(secret)).withAudience(audience).withIssuer(issuer).build()

        return try {
            val decodedJWT = verifier.verify(token)
            val userId = decodedJWT.getClaim("id").asInt()
            val username = decodedJWT.getClaim("username").asString()
            User(id = userId, username = username, passwordHash = "")
        } catch (e: Exception) {
            null
        }
    }
}