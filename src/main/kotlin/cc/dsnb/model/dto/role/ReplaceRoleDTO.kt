package cc.dsnb.model.dto.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceRoleDTO(
    val name: String,
    val description: String,
    @SerialName("is_admin")
    val isAdmin: Boolean
)