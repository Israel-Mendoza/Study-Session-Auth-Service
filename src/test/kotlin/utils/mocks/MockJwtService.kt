package dev.artisra.utils.mocks

import dev.artisra.auth.models.Token
import dev.artisra.models.User
import dev.artisra.services.interfaces.JwtService

class MockJwtService : JwtService {
    override fun generateToken(user: User) = Token(
        "mock-jwt-token-for-${user.username}", System.currentTimeMillis() + 3600000
    )

    override fun verifyToken(token: String): User? {
        TODO("Not yet implemented")
    }
}