package oathbreakers.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*
import java.time.Clock
import java.time.temporal.ChronoUnit

class JWTService(config: ApplicationConfig) {
    val issuer = config.property("jwt.domain").getString()
    val audience = config.property("jwt.audience").getString()
    val realm = config.property("jwt.realm").getString()
    private val secret = config.property("jwt.secret").getString()

    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun generateJWT(id: Int): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Clock.systemUTC().instant().plus(30, ChronoUnit.DAYS))
            .withClaim("id", id)
            .sign(algorithm)
    }
}