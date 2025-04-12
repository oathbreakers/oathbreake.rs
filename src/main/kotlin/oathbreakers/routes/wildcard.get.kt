package oathbreakers.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import oathbreakers.db.dbQuery
import oathbreakers.db.tables.UploadsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import java.io.File

fun Routing.wildcardGet() {
    val dataDir = File(environment.config.property("app.data_dir").getString())
    dataDir.mkdirs()

    get("/{slug}") {
        @Serializable
        data class UrlResponse(val destination: String)

        val slug = call.parameters["slug"]!!

        val row = dbQuery {
            UploadsTable.selectAll().where(UploadsTable.slug.eq(slug)).singleOrNull()
        }

        if (row == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val urlDest = row[UploadsTable.urlDestination]
            if (urlDest == null) {
                val file = File(dataDir, slug)
                if (!file.exists()) {
                    return@get call.respond(HttpStatusCode.NotFound)
                }

                call.respondBytesWriter(ContentType.defaultForFile(file), HttpStatusCode.OK) {
                    file.readChannel().copyAndClose(this)
                }
            } else {
                call.respond(HttpStatusCode.OK, UrlResponse(urlDest))
            }
        }
    }
}