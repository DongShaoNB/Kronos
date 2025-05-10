package cc.dsnb.model.dto.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoleDTO(
    val name: String? = null,
    val description: String? = null,
    @SerialName("is_admin")
    val isAdmin: Boolean? = null
)