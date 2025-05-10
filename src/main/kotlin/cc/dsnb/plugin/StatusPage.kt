package cc.dsnb.plugin

import cc.dsnb.model.ResponseCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        // RateLimit
        status(HttpStatusCode.TooManyRequests) { call, status ->
            val retryAfter = call.response.headers["Retry-After"]
            call.respond(ResponseCode.TOO_MANY_REQUESTS.apply { message = message.replace("{s}", retryAfter ?: "") })
        }
    }
}