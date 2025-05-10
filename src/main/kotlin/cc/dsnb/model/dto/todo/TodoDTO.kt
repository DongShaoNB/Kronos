package cc.dsnb.model.dto.todo

import cc.dsnb.model.dto.tag.TagDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoDTO(
    val id: Int,
    val title: String,
    val description: String?,
    val tags: List<TagDTO>?,
    @SerialName("user_id")
    val userId: Int,
    val completed: Boolean,
    @SerialName("due_date")
    val dueDate: String?,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)