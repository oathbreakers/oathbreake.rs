package oathbreakers.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import oathbreakers.routes.auth.authLoginPost
import oathbreakers.routes.auth.authRegisterPost
import oathbreakers.routes.index

fun Application.configureRouting() {
    routing {
        index()

        authRegisterPost()
        authLoginPost()
    }
}
