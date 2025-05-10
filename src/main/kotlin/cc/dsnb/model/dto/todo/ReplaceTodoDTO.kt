package cc.dsnb.model.dto.todo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceTodoDTO(
    val title: String,
    val description: String,
    val tags: List<Int>,
    val completed: Boolean,
    @SerialName("due_date")
    val dueDate: String
)