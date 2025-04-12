package oathbreakers.db.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val safeUsername = text("safe_username").index()
    val username = text("username")
    val passwordHash = text("password_hash")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}