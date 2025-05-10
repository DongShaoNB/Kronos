package cc.dsnb.model.dto.tag

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewTagDTO(
    val name: String,
    val description: String? = null,
    @SerialName("user_id")
    val userId: Int? = null
)