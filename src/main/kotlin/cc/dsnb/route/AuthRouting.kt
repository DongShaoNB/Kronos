package cc.dsnb.route

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.auth.LoginDTO
import cc.dsnb.model.dto.auth.RegisterDTO
import cc.dsnb.plugin.respond
import cc.dsnb.service.SystemService
import cc.dsnb.service.UserService
import cc.dsnb.util.StringUtil
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.util.*

fun Route.authRouting() {

    val userService by application.inject<UserService>()
    val systemService by application.inject<SystemService>()

    val config = environment.config
    val secret = config.property("jwt.secret").getString()
    val issuer = config.property("jwt.issuer").getString()
    val audience = config.property("jwt.audience").getString()

    route("auth") {

        post("register") {
            val registerDTO = call.receive<RegisterDTO>()
            val defaultRoleId =
                systemService.findSettingByKey("default_role_id")
                    ?: return@post call.respond(ResponseCode.INITIALIZE_FIRST)
            if (userService.findUserByUsername(registerDTO.username) != null) return@post call.respond(ResponseCode.USERNAME_IN_USE)
            if (!StringUtil.isValidEmail(registerDTO.email)) return@post call.respond(ResponseCode.EMAIL_INVALID)
            if (userService.findUserByEmail(registerDTO.email) != null) return@post call.respond(ResponseCode.EMAIL_IN_USE)
            if (registerDTO.password.length < 8) return@post call.respond(ResponseCode.PASSWORD_AT_LEAST_8_CHARACTERS)
            val userDTO = userService.createUser(
                username = registerDTO.username,
                avatar = null,
                name = registerDTO.name,
                email = registerDTO.email,
                roleId = defaultRoleId.value.toInt(),
                language = registerDTO.language,
                password = registerDTO.password,
                registerIp = call.request.origin.remoteHost
            ).toDTO()
            call.respond(ResponseCode.OK, userDTO)
        }

        post("login") {
            val loginDTO = call.receive<LoginDTO>()
            val userDAO = userService.findUserByUsername(loginDTO.username)
                ?: return@post call.respond(ResponseCode.USER_NOT_FOUND)
            // If the password is less than 8 characters, it must be wrong, just return directly
            if (loginDTO.password.length < 8) return@post call.respond(ResponseCode.INVALID_CREDENTIALS)
            if (userService.verifyUserPassword(userDAO.id.value, loginDTO.password)) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("user_id", userDAO.id.value)
                    // 604800000 is a week
                    .withExpiresAt(Date(System.currentTimeMillis() + 604800000))
                    .sign(Algorithm.HMAC256(secret))
                // Update the last login time and IP
                transaction {
                    userDAO.lastLoginAt = LocalDateTime.now()
                    userDAO.lastLoginIp = call.request.origin.remoteHost
                }
                call.respond(ResponseCode.OK, mapOf("token" to token))
            } else {
                call.respond(ResponseCode.INVALID_CREDENTIALS)
            }
        }

    }

}