package cc.dsnb.plugin

import cc.dsnb.route.*
import cc.dsnb.route.admin.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("api") {
            authRouting()
            systemRouting()
            authenticate("auth-jwt") {
                roleRouting()
                userRouting()
                tagRouting()
                todoRouting()
                route("admin") {
                    adminRoleRouting()
                    adminUserRouting()
                    adminTagRouting()
                    adminTodoRouting()
                }
            }
        }
    }
}