package oathbreakers.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import kotlin.system.exitProcess

private fun Application.getDataSource(): HikariDataSource {
    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = environment.config.property("database.uri").getString()
        username = environment.config.property("database.username").getString()
        password = environment.config.property("database.password").getString()
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        validate()
    }

    return HikariDataSource(config)
}

fun Application.configureDatabase() {
    var datasource: HikariDataSource? = null
    val flyway: Flyway?
    try {
        datasource = getDataSource()

        flyway = Flyway.configure()
            .validateOnMigrate(false)
            .locations("classpath:migrations")
            .dataSource(datasource)
            .load()
        flyway.migrate()

        Database.connect(datasource)
    } catch (e: Exception) {
        e.printStackTrace()
        datasource?.close()

        // FIXME: This just causes the engine to deadlock
        exitProcess(-1)
    }
}
