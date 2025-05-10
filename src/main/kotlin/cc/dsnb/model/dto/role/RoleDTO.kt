package cc.dsnb.model.dto.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleDTO(
    val id: Int,
    val name: String,
    val description: String?,
    @SerialName("is_admin")
    val isAdmin: Boolean,
    @SerialName("created_at")
    val createdAt: String
)