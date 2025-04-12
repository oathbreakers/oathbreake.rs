package oathbreakers.routes.auth

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import oathbreakers.db.dbQuery
import oathbreakers.db.tables.UsersTable
import oathbreakers.services.JWTService
import oathbreakers.utils.GenericError
import oathbreakers.utils.argon2
import oathbreakers.utils.hash
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertReturning
import org.koin.ktor.ext.inject

@Serializable
private data class RegisterRequest(val username: String, val password: String) {
    fun isUsernameValid(): Boolean {
        return username.length in 2..16 && username.all { it.isLetterOrDigit() }
    }

    fun isPasswordValid(): Boolean {
        return password.length in 8..128
    }
}

@Serializable
private data class RegisterResponse(val token: String)

fun Routing.authRegisterPost() {
    val jwtService by inject<JWTService>()

    post("/auth/register") {
        val request = call.receive<RegisterRequest>()
        if (!request.isUsernameValid()) {
            return@post call.respond(HttpStatusCode.PreconditionFailed, GenericError("INVALID_USERNAME"))
        }

        if (!request.isPasswordValid()) {
            return@post call.respond(HttpStatusCode.PreconditionFailed, GenericError("INVALID_PASSWORD"))
        }

        val safeUsername = request.username.lowercase()
        val existingUser = dbQuery {
            UsersTable.select(UsersTable.id).where(UsersTable.username.eq(safeUsername)).limit(1).firstOrNull()
        } != null
        if (existingUser) {
            return@post call.respond(HttpStatusCode.Conflict)
        }

        val hashedPassword = argon2.hash(request.password)

        val userId = dbQuery {
            UsersTable.insertReturning(listOf(UsersTable.id)) {
                it[UsersTable.safeUsername] = safeUsername
                it[username] = request.username
                it[passwordHash] = hashedPassword
            }.single()
        }[UsersTable.id]

        call.respond(HttpStatusCode.OK, RegisterResponse(jwtService.generateJWT(userId)))
    }
}