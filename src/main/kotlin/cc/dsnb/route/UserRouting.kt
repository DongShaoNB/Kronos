package cc.dsnb.route

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.user.AdminUpdateUserDTO
import cc.dsnb.model.dto.user.CommonUpdateUserDTO
import cc.dsnb.model.dto.user.UpdatePasswordDTO
import cc.dsnb.model.dto.user.VerifyEmailDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runWithUserId
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

fun Route.userRouting() {

    val userService by application.inject<UserService>()
    val systemService by application.inject<SystemService>()

    route("users") {

        get {
            call.runWithUserId { userId ->
                val userDTO =
                    userService.findUserById(userId)?.toDTO() ?: return@get call.respond(ResponseCode.INVALID_TOKEN)
                call.respond(ResponseCode.OK, userDTO)
            }
        }

        patch {
            call.runWithUserId { userId ->
                val commonUpdateUserDTO = call.receive<CommonUpdateUserDTO>()
                if (commonUpdateUserDTO.email != null && !StringUtil.isValidEmail(commonUpdateUserDTO.email)) return@patch call.respond(
                    ResponseCode.EMAIL_INVALID
                )
                val userDTO = userService.updateUser(userId, commonUpdateUserDTO)?.toDTO() ?: return@patch call.respond(
                    ResponseCode.USER_NOT_FOUND
                )
                call.respond(ResponseCode.OK, userDTO)
            }
        }

        post("password") {
            call.runWithUserId { userId ->
                val updatePasswordDTO = call.receive<UpdatePasswordDTO>()
                if (!userService.verifyUserPassword(
                        userId,
                        updatePasswordDTO.currentPassword
                    )
                ) return@post call.respond(ResponseCode.CURRENT_PASSWORD_INCORRECT)
                if (userService.verifyUserPassword(userId, updatePasswordDTO.newPassword)) return@post call.respond(
                    ResponseCode.NEW_PASSWORD_SAME_AS_CURRENT
                )
                userService.updateUserPassword(userId, updatePasswordDTO.newPassword)
                call.respond(ResponseCode.OK)
            }
        }

        post("avatars") {
            call.runWithUserId { userId ->
                val avatarPath =
                    systemService.findSettingByKey("avatar_path")?.value?.replace("{id}", userId.toString())
                        ?: return@post call.respond(ResponseCode.INITIALIZE_FIRST)
                val userAvatar =
                    userService.findUserById(userId)?.avatar
                        ?: return@post call.respond(ResponseCode.USER_NOT_FOUND)
                val timestamp = System.currentTimeMillis() / 1000
                val newAvatarName = if (userAvatar.startsWith("ver")) {
                    val num = userAvatar.split("_")[1].toInt() + 1
                    "ver_${num}_${timestamp}.png"
                } else {
                    "ver_1_${timestamp}.png"
                }

                val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 2)
                // Create directory
                println(File(avatarPath).mkdirs())
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val file = File(avatarPath + newAvatarName)
                            part.provider().copyAndClose(file.writeChannel())
                        }

                        else -> {}
                    }
                    part.dispose()
                }
                userService.updateUser(userId, AdminUpdateUserDTO(avatar = newAvatarName))
                call.respond(ResponseCode.OK)
            }
        }

        route("verification-codes") {

            post {
                call.runWithUserId { userId ->
                    if (userService.sendVerificationEmail(userId)) {
                        call.respond(ResponseCode.CREATED)
                    }
                }
            }

            post("verify") {
                call.runWithUserId { userId ->
                    val verifyEmailDTO = call.receive<VerifyEmailDTO>()
                    if (userService.verifyUserEmail(userId, verifyEmailDTO.code)) {
                        call.respond(ResponseCode.OK)
                    } else {
                        call.respond(ResponseCode.EMAIL_VERIFICATION_CODE_INVALID)
                    }
                }
            }

        }

        route("{id}") {

            get("avatars") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
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

        }

    }

}
