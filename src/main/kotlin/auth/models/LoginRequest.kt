package dev.artisra.auth.models

import kotlinx.serialization.Serializable

@Serializable
class LoginRequest(
    val username: String,
    val password: String,
)