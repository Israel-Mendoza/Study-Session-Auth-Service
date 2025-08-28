package dev.artisra.services.interfaces

import dev.artisra.auth.models.TokenPayload
import dev.artisra.models.User

interface JwtService {
    fun generateToken(user: User): TokenPayload
    fun verifyToken(token: String): User?
}