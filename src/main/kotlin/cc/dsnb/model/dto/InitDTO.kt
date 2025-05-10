package cc.dsnb.model.dto

import cc.dsnb.model.dto.role.NewRoleDTO
import cc.dsnb.model.dto.user.NewUserDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitDTO(
    @SerialName("avatar_path")
    val avatarPath: String? = "users/{id}/avatars",
    @SerialName("default_avatar")
    val defaultAvatar: String? = "users/default/avatars/default.png",
    @SerialName("default_role")
    val defaultRole: NewRoleDTO,
    @SerialName("default_admin_role")
    val defaultAdminRoleDTO: NewRoleDTO,
    @SerialName("default_admin")
    val defaultAdminDTO: NewUserDTO
)