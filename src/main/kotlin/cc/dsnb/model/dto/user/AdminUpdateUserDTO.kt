package cc.dsnb.model.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminUpdateUserDTO(
    val avatar: String? = null,
    val name: String? = null,
    val email: String? = null,
    @SerialName("email_verified")
    val emailVerified: Boolean? = null,
    @SerialName("role_id")
    val roleId: Int? = null,
    val language: String? = null
)