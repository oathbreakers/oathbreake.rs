package oathbreakers.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import oathbreakers.db.dbQuery
import oathbreakers.db.tables.UploadsTable
import oathbreakers.models.AuthenticatedUser
import oathbreakers.utils.authPost
import oathbreakers.utils.randomString
import org.jetbrains.exposed.sql.insert
import java.io.File
import java.net.URI
import java.net.URISyntaxException

fun Routing.uploadPost() {
    val dataDir = File(environment.config.property("app.data_dir").getString())
    dataDir.mkdirs()

    authPost("/upload") {
        @Serializable
        data class Response(val url: String)

        var isFile = false
        var isUrl = false
        var input: String? = null

        var slug = randomString(5)

        val mp = call.receiveMultipart()
        mp.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "input" -> {
                        if(part.value.isNotBlank()) {
                            try {
                                input = part.value

                                when (URI(part.value).scheme) {
                                    "http", "https" -> {
                                        isUrl = true
                                    }
                                }
                            } catch (ignored: URISyntaxException) {
                            }
                        }
                    }
                }

                is PartData.FileItem -> when (part.name) {
                    "file" -> {
                        isFile = true

                        var ext = ""
                        val ogName = part.originalFileName
                        if (ogName != null && ogName.contains(".")) {
                            ext = "." + ogName.split(".").last()
                        }

                        slug = "$slug$ext"

                        val file = File(dataDir, slug)
                        part.provider().copyAndClose(file.writeChannel())
                    }
                }

                else -> {
                    println("Unknown form item ${part.name}")
                }
            }

            part.dispose()
        }

        if (!isFile && !isUrl) {
            return@authPost call.respond(HttpStatusCode.BadRequest)
        }

        val user = call.principal<AuthenticatedUser>()!!

        dbQuery {
            UploadsTable.insert {
                it[uploader] = user.id
                it[UploadsTable.slug] = slug
                if (isUrl) {
                    it[urlDestination] = input!!
                }
            }
        }

        call.respond(HttpStatusCode.OK, Response("https://oathbreake.rs/${slug}"))
    }
}