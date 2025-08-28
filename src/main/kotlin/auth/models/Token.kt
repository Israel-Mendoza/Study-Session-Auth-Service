package dev.artisra.auth.models

data class Token(
    val token: String,
    val expiration: Long,
)