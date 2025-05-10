package cc.dsnb.model.dto.tag

import kotlinx.serialization.Serializable

@Serializable
data class ReplaceTagDTO(
    val name: String,
    val description: String
)