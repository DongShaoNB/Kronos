package cc.dsnb.route.admin

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.user.AdminUpdateUserDTO
import cc.dsnb.model.dto.user.NewUserDTO
import cc.dsnb.model.dto.user.ReplaceUserDTO
import cc.dsnb.model.dto.user.UpdatePasswordDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runIfAdmin
import cc.dsnb.service.RoleService
import cc.dsnb.service.SystemService
import cc.dsnb.service.UserService
import cc.dsnb.util.StringUtil
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.adminUserRouting() {

    val userService by application.inject<UserService>()
    val roleService by application.inject<RoleService>()
    val systemService by application.inject<SystemService>()

    route("users") {

        post {
            call.runIfAdmin {
                val newUser = call.receive<NewUserDTO>()
                if (userService.findUserByUsername(newUser.username) != null) return@post call.respond(ResponseCode.USERNAME_IN_USE)
                if (!StringUtil.isValidEmail(newUser.email)) return@post call.respond(ResponseCode.EMAIL_INVALID)
                if (userService.findUserByEmail(newUser.email) != null) return@post call.respond(ResponseCode.EMAIL_IN_USE)
                val newUserRoleId = newUser.roleId?.let { roleId ->
                    // Check if the role exists
                    roleService.findRoleById(roleId)?.id?.value ?: return@post call.respond(ResponseCode.ROLE_NOT_FOUND)
                } ?: systemService.findSettingByKey("default_role_id")?.value?.toInt() ?: return@post call.respond(
                    ResponseCode.INITIALIZE_FIRST
                )
                if (newUser.password.length < 8) return@post call.respond(ResponseCode.PASSWORD_AT_LEAST_8_CHARACTERS)
                val userDTO = userService.createUser(
                    username = newUser.username,
                    avatar = newUser.avatar,
                    name = newUser.name,
                    email = newUser.email,
                    roleId = newUserRoleId,
                    language = newUser.language,
                    password = newUser.password,
                    registerIp = newUser.registerIp
                ).toDTO()
                call.respond(ResponseCode.CREATED, userDTO)
            }
        }

        get {
            call.runIfAdmin {
                val username = call.queryParameters["username"]
                val email = call.queryParameters["email"]
                if (!email.isNullOrBlank() && !StringUtil.isValidEmail(email)) return@get call.respond(ResponseCode.EMAIL_INVALID)
                val userDTO = userService.findAllUsers()
                    .filter {
                        (username.isNullOrBlank() || it.username.contains(username)) && (email.isNullOrBlank() || it.email.contains(
                            email
                        ))
                    }
                    .map { it.toDTO() }
                call.respond(ResponseCode.OK, userDTO)
            }
        }

        route("{id}") {

            get {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                    val userDTO =
                        userService.findUserById(id)?.toDTO() ?: return@get call.respond(ResponseCode.USER_NOT_FOUND)
                    call.respond(ResponseCode.OK, userDTO)
                }
            }

            patch {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(ResponseCode.INVALID_ID)
                    val adminUpdateUserDTO = call.receive<AdminUpdateUserDTO>()
                    if (adminUpdateUserDTO.email != null && !StringUtil.isValidEmail(adminUpdateUserDTO.email)) return@patch call.respond(
                        ResponseCode.EMAIL_INVALID
                    )
                    if (adminUpdateUserDTO.roleId != null && roleService.findRoleById(adminUpdateUserDTO.roleId) == null) return@patch call.respond(
                        ResponseCode.ROLE_NOT_FOUND
                    )
                    val userDTO = userService.updateUser(id, adminUpdateUserDTO)?.toDTO() ?: return@patch call.respond(
                        ResponseCode.USER_NOT_FOUND
                    )
                    call.respond(ResponseCode.OK, userDTO)
                }
            }

            put {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(ResponseCode.INVALID_ID)
                    val replaceUserDTO = call.receive<ReplaceUserDTO>()
                    if (!StringUtil.isValidEmail(replaceUserDTO.email)) return@put call.respond(ResponseCode.EMAIL_INVALID)
                    if (roleService.findRoleById(replaceUserDTO.roleId) == null) return@put call.respond(ResponseCode.ROLE_NOT_FOUND)
                    val userDTO = userService.replaceUser(id, replaceUserDTO)?.toDTO() ?: return@put call.respond(
                        ResponseCode.USER_NOT_FOUND
                    )
                    call.respond(ResponseCode.OK, userDTO)
                }
            }

            delete {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(ResponseCode.INVALID_ID)
                    userService.findUserById(id) ?: return@delete call.respond(ResponseCode.USER_NOT_FOUND)
                    userService.deleteUser(id)
                    call.respond(ResponseCode.NO_CONTENT)
                }
            }

            patch("password") {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(ResponseCode.INVALID_ID)
                    val updatePasswordDTO = call.receive<UpdatePasswordDTO>()
                    if (!userService.verifyUserPassword(
                            id,
                            updatePasswordDTO.currentPassword
                        )
                    ) return@patch call.respond(
                        ResponseCode.CURRENT_PASSWORD_INCORRECT
                    )
                    if (userService.verifyUserPassword(id, updatePasswordDTO.newPassword)) return@patch call.respond(
                        ResponseCode.NEW_PASSWORD_SAME_AS_CURRENT
                    )
                    userService.updateUserPassword(id, updatePasswordDTO.newPassword)
                    call.respond(ResponseCode.OK)
                }
            }

            route("avatars") {
                get {
                    val id =
                        call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                    val avatarPath = systemService.findSettingByKey("avatar_path")?.value
                        ?: return@get call.respond(ResponseCode.INITIALIZE_FIRST)
                    val userAvatar =
                        userService.findUserById(id)?.avatar ?: return@get call.respond(ResponseCode.USER_NOT_FOUND)
                    File(avatarPath.replace("{id}", id.toString()) + userAvatar).also {
                        if (it.exists()) {
                            call.respondFile(it)
                        } else {
                            call.respondFile(
                                File(
                                    systemService.findSettingByKey("default_avatar")?.value
                                        ?: return@get call.respond(ResponseCode.INITIALIZE_FIRST)
                                )
                            )
                        }
                    }
                }

                post {
                    call.runIfAdmin {
                        val id =
                            call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(ResponseCode.INVALID_ID)
                        val avatarPath = systemService.findSettingByKey("avatar_path")?.value
                            ?: return@post call.respond(ResponseCode.INITIALIZE_FIRST)
                        val userAvatar =
                            userService.findUserById(id)?.avatar
                                ?: return@post call.respond(ResponseCode.USER_NOT_FOUND)
                        val timestamp = System.currentTimeMillis() / 1000
                        var newAvatarName = if (userAvatar.startsWith("ver")) {
                            val num = userAvatar.split("_")[1].toInt() + 1
                            "ver_${num}_${timestamp}.png"
                        } else {
                            "ver_1_${timestamp}.png"
                        }

                        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 2)
                        // Create directory
                        println(File(avatarPath.replace("{id}", id.toString())).mkdirs())
                        multipartData.forEachPart { part ->
                            when (part) {
                                is PartData.FileItem -> {
                                    val file = File(avatarPath.replace("{id}", id.toString()) + newAvatarName)
                                    part.provider().copyAndClose(file.writeChannel())
                                }

                                else -> {}
                            }
                            part.dispose()
                        }
                        userService.updateUser(id, AdminUpdateUserDTO(avatar = newAvatarName))
                        call.respond(ResponseCode.OK)
                    }
                }

            }

        }

    }


}