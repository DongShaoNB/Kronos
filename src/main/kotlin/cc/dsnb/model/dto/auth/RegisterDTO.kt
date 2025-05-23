package cc.dsnb.model.dto.auth

import kotlinx.serialization.Serializable


@Serializable
data class RegisterDTO(
    val username: String,
    val name: String,
    val email: String,
    val language: String? = null,
    val password: String
)