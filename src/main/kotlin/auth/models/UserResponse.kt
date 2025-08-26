package dev.artisra.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(val id: Int, val username: String)