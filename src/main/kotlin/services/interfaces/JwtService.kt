package dev.artisra.services.interfaces

import dev.artisra.auth.models.Token
import dev.artisra.models.User

interface JwtService {
    fun generateToken(user: User): Token
    fun verifyToken(token: String): User?
}