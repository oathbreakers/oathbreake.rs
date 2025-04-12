package oathbreakers.utils

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oathbreakers.models.AuthenticatedUser

fun Routing.authRoute(path: String, method: HttpMethod, block: RoutingHandler) {
    authenticate {
        route(path) {
            method(method) {
                handle {
                    val principal = call.principal<AuthenticatedUser>()
                    if (principal != null) {
                        block()
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }
        }
    }
}

fun Routing.authGet(path: String, block: RoutingHandler) = authRoute(path, HttpMethod.Get, block)
fun Routing.authPost(path: String, block: RoutingHandler) = authRoute(path, HttpMethod.Put, block)
fun Routing.authPut(path: String, block: RoutingHandler) = authRoute(path, HttpMethod.Put, block)
