package cc.dsnb.model.dto.todo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTodoDTO(
    val title: String? = null,
    val description: String? = null,
    val tags: List<Int>? = null,
    val completed: Boolean? = null,
    @SerialName("due_date")
    val dueDate: String? = null
)