package dev.artisra.auth.models

import kotlinx.serialization.Serializable

@Serializable
class LoginResponse(
    val message: String,
    val timestamp: String,
    val token: String?,
    val expiration: Long?
)