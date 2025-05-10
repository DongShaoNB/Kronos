package cc.dsnb.model.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewUserDTO(
    val username: String,
    val avatar: String? = null,
    val name: String,
    val email: String,
    @SerialName("role_id")
    val roleId: Int? = null,
    val language: String? = null,
    val password: String,
    @SerialName("register_ip")
    val registerIp: String,
)