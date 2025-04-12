package oathbreakers.plugins

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import oathbreakers.services.JWTService
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtService by inject<JWTService>()

    authentication {
        jwt {
            realm = jwtService.realm
            verifier(
                JWT
                    .require(jwtService.algorithm)
                    .withAudience(jwtService.audience)
                    .withIssuer(jwtService.issuer)
                    .build()
            )

            validate { credential ->
                // TODO: Replace with our own User principal
                if (credential.payload.audience.contains(jwtService.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
