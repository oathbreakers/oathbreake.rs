package oathbreakers.plugins

import io.ktor.server.application.*
import oathbreakers.services.JWTService
import org.koin.dsl.module
import org.koin.ktor.plugin.koin

fun Application.configureServices() {
    koin {
        modules(module {
            single { JWTService(environment.config) }
        })
    }
}