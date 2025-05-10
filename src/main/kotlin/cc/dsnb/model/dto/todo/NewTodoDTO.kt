package cc.dsnb.model.dto.todo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewTodoDTO(
    val title: String,
    val description: String? = null,
    val tags: List<Int>? = null,
    @SerialName("user_id")
    val userId: Int? = null,
    @SerialName("due_date")
    val dueDate: String? = null
)