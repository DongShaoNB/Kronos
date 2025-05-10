package cc.dsnb.model.dto.tag

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTagDTO(
    val name: String? = null,
    val description: String? = null
)