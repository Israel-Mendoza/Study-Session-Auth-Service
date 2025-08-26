package dev.artisra.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val username: String,
    val password: String,
    val email: String,
)