package oathbreakers.plugins

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.forwardedheaders.*

fun Application.configureHTTP() {
    val devMode = developmentMode

    install(ForwardedHeaders)
    install(XForwardedHeaders)
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHost("oathbreake.rs")

        if (devMode) {
            anyHost()
        }
    }
    install(Compression)
    install(ContentNegotiation) {
        json()
    }
}
