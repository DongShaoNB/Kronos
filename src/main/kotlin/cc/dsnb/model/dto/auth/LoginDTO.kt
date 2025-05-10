package cc.dsnb.model.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val username: String,
    val password: String
)