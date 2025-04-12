package oathbreakers.routes.profile

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import oathbreakers.db.dbQuery
import oathbreakers.db.tables.UsersTable
import oathbreakers.models.AuthenticatedUser
import oathbreakers.utils.authGet
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll

@Serializable
private data class MeResponse(val id: Int, val username: String)

fun Routing.profileMeGet() {
    authGet("/profile/me") {
        val id = call.principal<AuthenticatedUser>()!!.id
        val row = dbQuery { UsersTable.selectAll().where(UsersTable.id.eq(id)).single() }
        call.respond(HttpStatusCode.OK, MeResponse(row[UsersTable.id], row[UsersTable.username]))
    }
}