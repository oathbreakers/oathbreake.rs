package oathbreakers.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.index() {
    get {
        call.respondText(
            """
                <h1 class="color: pink;">Trans rights 🏳️‍⚧️🏳️‍⚧️🏳️‍⚧️🏳️‍⚧️🏳️‍⚧️🏳️‍⚧️
        """.trimIndent(), ContentType.Text.Html
        )
    }
}