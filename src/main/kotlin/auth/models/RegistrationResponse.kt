package dev.artisra.auth.models

import dev.artisra.auth.models.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationResponse(
    val message: String,
    val timestamp: String,
    val user: UserResponse?,
)