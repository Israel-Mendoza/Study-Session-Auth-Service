package dev.artisra.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.artisra.auth.models.TokenPayload
import dev.artisra.models.User
import java.util.*

class JwtService(
    private val audience: String,
    private val issuer: String,
    private val secret: String,
) {

    // In a real application, these should be in environment variables or config files

    fun generateToken(user: User): TokenPayload {
        val expiration = Date(System.currentTimeMillis() + 600000) // Token expires in 10 minutes

        val token = JWT.create().withAudience(audience).withIssuer(issuer).withClaim("id", user.id)
            .withClaim("username", user.username).withExpiresAt(expiration) // Token expires in 10 minutes
            .sign(Algorithm.HMAC256(secret))

        return TokenPayload(token = token, expiration = expiration.time)
    }

    fun verifyToken(token: String): User? {
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