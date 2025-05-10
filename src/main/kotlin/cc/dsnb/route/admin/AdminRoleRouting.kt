package cc.dsnb.route.admin

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.role.NewRoleDTO
import cc.dsnb.model.dto.role.ReplaceRoleDTO
import cc.dsnb.model.dto.role.UpdateRoleDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runIfAdmin
import cc.dsnb.service.RoleService
import cc.dsnb.service.SystemService
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminRoleRouting() {

    val roleService by application.inject<RoleService>()
    val systemService by application.inject<SystemService>()

    route("roles") {

        post {
            call.runIfAdmin {
                val newRoleDTO = call.receive<NewRoleDTO>()
                if (roleService.findRoleByName(newRoleDTO.name) != null) return@post call.respond(ResponseCode.ROLE_NAME_IN_USE)
                val roleDTO =
                    roleService.createRole(newRoleDTO.name, newRoleDTO.description, newRoleDTO.isAdmin).toDTO()
                call.respond(ResponseCode.CREATED, roleDTO)
            }
        }

        get {
            call.runIfAdmin {
                val name = call.queryParameters["name"]
                val description = call.queryParameters["description"]
                val roleDTO = roleService.findAllRoles().filter {
                    (name.isNullOrBlank() || it.name.contains(
                        name,
                        true
                    )) && (description.isNullOrBlank() || it.description?.contains(
                        description, true
                    ) == true)
                }.map { it.toDTO() }
                call.respond(ResponseCode.OK, roleDTO)
            }
        }

        route("{id}") {

            get {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                    val roleDTO =
                        roleService.findRoleById(id)?.toDTO() ?: return@get call.respond(ResponseCode.ROLE_NOT_FOUND)
                    call.respond(ResponseCode.OK, roleDTO)
                }
            }

            patch {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(ResponseCode.INVALID_ID)
                    val updateRoleDTO = call.receive<UpdateRoleDTO>()
                    val roleDTO = roleService.updateRole(id, updateRoleDTO)?.toDTO()
                        ?: return@patch call.respond(ResponseCode.ROLE_NOT_FOUND)
                    call.respond(ResponseCode.OK, roleDTO)
                }
            }

            put {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(ResponseCode.INVALID_ID)
                    val replaceRoleDTO = call.receive<ReplaceRoleDTO>()
                    if (replaceRoleDTO.name.isBlank()) return@put call.respond(ResponseCode.ROLE_NAME_CAN_NOT_BE_EMPTY)
                    val roleDTO = roleService.replaceRole(id, replaceRoleDTO)?.toDTO()
                        ?: return@put call.respond(ResponseCode.ROLE_NOT_FOUND)
                    call.respond(ResponseCode.OK, roleDTO)
                }
            }

            delete {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(ResponseCode.INVALID_ID)
                    roleService.findRoleById(id) ?: return@delete call.respond(ResponseCode.ROLE_NOT_FOUND)
                    val defaultRoleId = systemService.findSettingByKey("default_role_id")?.value?.toIntOrNull()
                        ?: return@delete call.respond(ResponseCode.INITIALIZE_FIRST)
                    val defaultAdminRoleId =
                        systemService.findSettingByKey("default_admin_role_id")?.value?.toIntOrNull()
                            ?: return@delete call.respond(ResponseCode.INITIALIZE_FIRST)
                    // Can not delete default role
                    if (id == defaultRoleId || id == defaultAdminRoleId) return@delete call.respond(ResponseCode.CAN_NOT_DELETE_DEFAULT_ROLE)
                    roleService.deleteRole(id)
                    call.respond(ResponseCode.NO_CONTENT)
                }
            }

        }

    }

}