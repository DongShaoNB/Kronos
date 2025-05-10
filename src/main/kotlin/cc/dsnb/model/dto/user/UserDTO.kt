package cc.dsnb.model.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val username: String,
    val avatar: String,
    val name: String,
    val email: String,
    @SerialName("email_verified")
    val emailVerified: Boolean,
    @SerialName("role_id")
    val roleId: Int,
    val language: String,
    @SerialName("register_ip")
    val registerIp: String,
    @SerialName("registered_at")
    val registeredAt: String,
    @SerialName("last_login_ip")
    val lastLoginIp: String?,
    @SerialName("last_login_at")
    val lastLoginAt: String
)