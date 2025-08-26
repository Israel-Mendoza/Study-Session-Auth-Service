package dev.artisra.auth.models

import kotlinx.serialization.Serializable

@Serializable
class TokenPayload(
    val token: String = "",
    val expiration: Long = 0L
)