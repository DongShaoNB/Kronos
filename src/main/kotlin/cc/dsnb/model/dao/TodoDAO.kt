package cc.dsnb.model.dao

import cc.dsnb.database.table.TodoTable
import cc.dsnb.database.table.TodoTagTable
import cc.dsnb.model.dto.todo.TodoDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class TodoDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<TodoDAO>(TodoTable)

    var title by TodoTable.title
    var description by TodoTable.description
    var tags by TagDAO via TodoTagTable
    var userId by TodoTable.userId
    var completed by TodoTable.completed
    var dueDate by TodoTable.dueDate
    var createdAt by TodoTable.createdAt
    var updatedAt by TodoTable.updatedAt

    fun toDTO() = transaction {
        TodoDTO(
            id = this@TodoDAO.id.value,
            title = title,
            description = description,
            tags = tags.map { it.toDTO() },
            userId = userId.value,
            completed = completed,
            dueDate = dueDate.toString(),
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString()
        )
    }

}