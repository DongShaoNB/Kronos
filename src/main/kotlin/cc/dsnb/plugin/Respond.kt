package cc.dsnb.plugin

import cc.dsnb.model.Response
import cc.dsnb.model.ResponseCode
import cc.dsnb.service.RoleService
import cc.dsnb.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

suspend fun ApplicationCall.respond(responseCode: ResponseCode) {
    respond(responseCode.httpStatus, Response(responseCode.code, responseCode.message, responseCode.data))
}

suspend inline fun <reified T : Any> ApplicationCall.respond(responseCode: ResponseCode, data: T) {
    respond(responseCode.httpStatus, Response(responseCode.code, responseCode.message, data))
}

suspend inline fun <reified T : Any> ApplicationCall.respond(responseCode: ResponseCode, data: List<T>) {
    respond(responseCode.httpStatus, Response(responseCode.code, responseCode.message, data))
}

suspend inline fun ApplicationCall.runWithUserId(block: (userId: Int) -> Unit) {
    val principal = principal<JWTPrincipal>() ?: return respond(ResponseCode.UNAUTHORIZED)
    val userId = principal.payload.getClaim("user_id").asInt() ?: return respond(ResponseCode.UNAUTHORIZED)
    block(userId)
}

suspend inline fun ApplicationCall.runIfAdmin(block: (isAdmin: Boolean) -> Unit) {
    val principal = principal<JWTPrincipal>() ?: return respond(ResponseCode.NO_PERMISSION)
    val userId = principal.payload.getClaim("user_id").asInt() ?: return respond(ResponseCode.NO_PERMISSION)

    val roleService by application.inject<RoleService>()
    val userService by application.inject<UserService>()

    val role = roleService.findRoleById(
        userService.findUserById(userId)?.roleId?.value ?: return respond(ResponseCode.NO_PERMISSION)
    )

    if (role?.isAdmin == true) {
        block(true)
    } else {
        respond(ResponseCode.NO_PERMISSION)
    }
}
