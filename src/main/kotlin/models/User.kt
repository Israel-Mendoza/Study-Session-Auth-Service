package dev.artisra.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val passwordHash: String // The securely hashed password
)