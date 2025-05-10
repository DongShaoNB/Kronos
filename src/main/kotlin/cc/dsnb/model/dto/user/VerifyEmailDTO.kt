package cc.dsnb.model.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailDTO(
    val code: String
)
