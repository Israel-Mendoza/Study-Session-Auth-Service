package dev.artisra.mappers

import dev.artisra.database.tables.UserTable
import dev.artisra.models.UserRecord
import org.jetbrains.exposed.v1.core.ResultRow

object UserMapper {
    fun fromResultRowToRecord(resultRow: ResultRow) = UserRecord(
        id = resultRow[UserTable.id],
        username = resultRow[UserTable.username],
        email = resultRow[UserTable.email],
        passwordHash = resultRow[UserTable.passwordHash]
    )
}