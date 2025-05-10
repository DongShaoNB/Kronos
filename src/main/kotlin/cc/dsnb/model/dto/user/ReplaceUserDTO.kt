package cc.dsnb.model.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceUserDTO(
    val avatar: String,
    val name: String,
    val email: String,
    @SerialName("email_verified")
    val emailVerified: Boolean,
    @SerialName("role_id")
    val roleId: Int,
    val language: String
)
