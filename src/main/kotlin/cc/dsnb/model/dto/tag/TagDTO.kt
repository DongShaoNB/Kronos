package cc.dsnb.model.dto.tag

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagDTO(
    val id: Int,
    val name: String,
    val description: String?,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("created_at")
    val createdAt: String
)