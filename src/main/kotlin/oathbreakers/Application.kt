package oathbreakers

import io.ktor.server.application.*
import oathbreakers.plugins.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.module() {
    install(Koin) {
        slf4jLogger()
    }

    configureDatabase()
    configureServices()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
