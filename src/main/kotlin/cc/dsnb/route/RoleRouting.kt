package cc.dsnb.route

import cc.dsnb.model.ResponseCode
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runWithUserId
import cc.dsnb.service.RoleService
import cc.dsnb.service.UserService
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.roleRouting() {

    val userService by application.inject<UserService>()
    val roleService by application.inject<RoleService>()

    route("roles") {

        get {
            call.runWithUserId { userId ->
                val userRoleId = userService.findUserById(userId)?.roleId?.value
                    ?: return@get call.respond(ResponseCode.INVALID_TOKEN)
                val roleDTO = roleService.findRoleById(userRoleId)?.toDTO()
                    ?: return@get call.respond(ResponseCode.ROLE_NOT_FOUND)
                call.respond(ResponseCode.OK, roleDTO)
            }
        }


    }

}