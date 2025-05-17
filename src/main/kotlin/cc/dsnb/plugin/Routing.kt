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
            authenticate("auth-jwt") {
                systemRouting()
                roleRouting()
                userRouting()
                tagRouting()
                todoRouting()
                aiRouting()
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