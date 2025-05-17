package cc.dsnb.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AIPromptDTO(
    val prompt: String
)