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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.ktor.ext.inject

@Serializable
private data class LoginRequest(val username: String, val password: String) {
    fun isUsernameValid(): Boolean {
        return username.length in 2..16 && username.all { it.isLetterOrDigit() }
    }

    fun isPasswordValid(): Boolean {
        return password.length in 8..128
    }
}

@Serializable
private data class LoginResponse(val token: String)

// This should be returned for almost every login invalid state
// to reduce the information people have.
private const val INVALID_LOGIN = "INVALID_LOGIN"

fun Routing.authLoginPost() {
    val invalidLoginErr = GenericError(INVALID_LOGIN)
    val jwtService by inject<JWTService>()

    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        if (!request.isUsernameValid()) {
            return@post call.respond(HttpStatusCode.BadRequest, invalidLoginErr)
        }

        if (!request.isPasswordValid()) {
            return@post call.respond(HttpStatusCode.BadRequest, invalidLoginErr)
        }

        val safeUsername = request.username.lowercase()
        val existingUser = dbQuery {
            UsersTable.select(UsersTable.id, UsersTable.passwordHash).where(UsersTable.username.eq(safeUsername))
                .limit(1).firstOrNull()
        }
        if (existingUser == null) {
            return@post call.respond(HttpStatusCode.BadRequest, invalidLoginErr)
        }

        val correctPassword = argon2.verify(existingUser[UsersTable.passwordHash], request.password.encodeToByteArray())
        if (!correctPassword) {
            return@post call.respond(HttpStatusCode.BadRequest)
        }

        call.respond(HttpStatusCode.OK, LoginResponse(jwtService.generateJWT(existingUser[UsersTable.id])))
    }
}