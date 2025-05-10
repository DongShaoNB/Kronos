package cc.dsnb.model.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class CommonUpdateUserDTO(
    val name: String? = null,
    val email: String? = null,
    val language: String? = null
)
