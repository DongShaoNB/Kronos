package cc.dsnb.model.dto.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewRoleDTO(
    val name: String,
    val description: String? = null,
    @SerialName("is_admin")
    val isAdmin: Boolean? = null
)