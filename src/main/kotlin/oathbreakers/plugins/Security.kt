package oathbreakers.plugins

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import oathbreakers.db.dbQuery
import oathbreakers.db.tables.UsersTable
import oathbreakers.models.AuthenticatedUser
import oathbreakers.services.JWTService
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtService by inject<JWTService>()

    authentication {
        jwt {
            realm = jwtService.realm
            verifier(
                JWT.require(jwtService.algorithm).withAudience(jwtService.audience).withIssuer(jwtService.issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.audience.contains(jwtService.audience)) {
                    val id = credential.payload.getClaim("id").asInt()

                    val userExists = dbQuery {
                        UsersTable.select(UsersTable.id).where(UsersTable.id.eq(id)).limit(1).singleOrNull()
                    } != null
                    if (userExists) {
                        this@configureSecurity.log.info("success auth $id")
                        AuthenticatedUser(id)
                    } else {
                        this@configureSecurity.log.warn("User $id authenticated with a valid JWT, but doesn't exist")
                        null
                    }
                } else {
                    null
                }
            }
        }
    }
}
