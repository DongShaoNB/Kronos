package cc.dsnb.route.admin

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.InitDTO
import cc.dsnb.model.dto.setting.NewSettingDTO
import cc.dsnb.model.dto.setting.UpdateSettingDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runIfAdmin
import cc.dsnb.service.RoleService
import cc.dsnb.service.SystemService
import cc.dsnb.service.UserService
import cc.dsnb.util.FileUtil
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.systemRouting() {

    val roleService by application.inject<RoleService>()
    val userService by application.inject<UserService>()
    val systemService by application.inject<SystemService>()

    route("system") {

        post("init") {
            if (systemService.findSettingByKey("is_initialized")?.value == "true") return@post call.respond(ResponseCode.ALREADY_INITIALIZED)
            val initDTO = call.receive<InitDTO>()
            val avatarPath = initDTO.avatarPath
            if (!avatarPath!!.contains("{id}")) return@post call.respond(ResponseCode.AVATAR_PATH_MUST_CONTAIN_ID)
            val defaultAvatar = initDTO.defaultAvatar!!
            // Save default avatar
            FileUtil.saveResource("default-avatar.png", File(defaultAvatar))
            val defaultRoleDTO = initDTO.defaultRole
            val defaultAdminRoleDTO = initDTO.defaultAdminRoleDTO
            val defaultAdminDTO = initDTO.defaultAdminDTO
            val defaultRoleDAO = roleService.createRole(defaultRoleDTO.name, defaultRoleDTO.description, false)
            val defaultAdminRoleDAO =
                roleService.createRole(defaultAdminRoleDTO.name, defaultAdminRoleDTO.description, true)
            userService.createUser(
                defaultAdminDTO.username,
                defaultAdminDTO.avatar,
                defaultAdminDTO.name,
                defaultAdminDTO.email,
                defaultAdminRoleDAO.id.value,
                defaultAdminDTO.language,
                defaultAdminDTO.password,
                defaultAdminDTO.registerIp
            )
            systemService.createSetting("is_initialized", "true")
            systemService.createSetting("avatar_path", avatarPath)
            systemService.createSetting("default_avatar", defaultAvatar)
            systemService.createSetting("default_role_id", defaultRoleDAO.id.value.toString())
            systemService.createSetting("default_admin_role_id", defaultAdminRoleDAO.id.value.toString())
            return@post call.respond(ResponseCode.OK)
        }

        authenticate("auth-jwt") {

            route("settings") {

                post {
                    call.runIfAdmin {
                        val newSettingDTO = call.receive<NewSettingDTO>()
                        if (systemService.findSettingByKey(newSettingDTO.key) != null) return@post call.respond(
                            ResponseCode.SETTING_KEY_EXIST
                        )
                        val settingDTO = systemService.createSetting(newSettingDTO.key, newSettingDTO.value).toDTO()
                        call.respond(ResponseCode.CREATED, settingDTO)
                    }
                }

                get {
                    call.runIfAdmin {
                        val key = call.queryParameters["key"]
                        val settingDTO = systemService.findAllSettings().filter { key.isNullOrBlank() || it.key == key }
                            .map { it.toDTO() }
                        call.respond(ResponseCode.OK, settingDTO)
                    }
                }

                route("{id}") {

                    get {
                        call.runIfAdmin {
                            val id =
                                call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                            val settingDTO = systemService.findSettingById(id)?.toDTO() ?: return@get call.respond(
                                ResponseCode.SETTING_NOT_FOUND
                            )
                            call.respond(ResponseCode.OK, settingDTO)
                        }
                    }

                    patch {
                        call.runIfAdmin {
                            val id = call.parameters["id"]?.toIntOrNull()
                                ?: return@patch call.respond(ResponseCode.INVALID_ID)
                            val updateSettingDTO = call.receive<UpdateSettingDTO>()
                            val settingDTO =
                                systemService.updateSetting(id, updateSettingDTO)?.toDTO() ?: return@patch call.respond(
                                    ResponseCode.SETTING_NOT_FOUND
                                )
                            call.respond(ResponseCode.OK, settingDTO)
                        }
                    }

                }

            }

        }

    }

}