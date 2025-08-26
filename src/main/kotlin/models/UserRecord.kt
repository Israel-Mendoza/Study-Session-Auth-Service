package dev.artisra.models

import kotlinx.serialization.Serializable

@Serializable
data class UserRecord(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val passwordHash: String = "",
)