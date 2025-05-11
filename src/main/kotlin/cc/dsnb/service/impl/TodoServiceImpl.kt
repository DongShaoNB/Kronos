package cc.dsnb.service.impl

import cc.dsnb.database.table.TagTable
import cc.dsnb.database.table.TodoTable
import cc.dsnb.database.table.TodoTagTable
import cc.dsnb.model.dao.TodoDAO
import cc.dsnb.model.dao.UserDAO
import cc.dsnb.model.dto.todo.ReplaceTodoDTO
import cc.dsnb.model.dto.todo.UpdateTodoDTO
import cc.dsnb.service.TodoService
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class TodoServiceImpl : TodoService {

    override fun createTodo(
        title: String,
        description: String?,
        tags: List<Int>?,
        dueDate: LocalDate?,
        userId: Int,
    ): TodoDAO = transaction {
        val todoDAO = TodoDAO.new {
            this.title = title
            this.description = description
            this.dueDate = dueDate
            this.userId = UserDAO.findById(userId)!!.id
        }
        if (!tags.isNullOrEmpty()) {
            tags.forEach { eachTagId ->
                TodoTagTable.insert {
                    it[todoId] = todoDAO.id
                    it[tagId] = TagTable.select(TagTable.id).where { TagTable.id eq eachTagId }
                }
            }
        }
        todoDAO
    }

    override fun findTodoById(id: Int): TodoDAO? = transaction { TodoDAO.findById(id) }

    override fun findTodoByTitle(title: String): List<TodoDAO> = transaction {
        TodoDAO.find { TodoTable.title eq title }.toList()
    }

    override fun findAllTodos(): List<TodoDAO> = transaction { TodoDAO.all().toList() }

    override fun updateTodo(id: Int, updateTodoDTO: UpdateTodoDTO): TodoDAO? = transaction {
        TodoDAO.findByIdAndUpdate(id) {
            if (updateTodoDTO.title != null) it.title = updateTodoDTO.title
            if (updateTodoDTO.description != null) it.description = updateTodoDTO.description
            if (updateTodoDTO.tags != null) {
                TodoTagTable.deleteWhere { TodoTagTable.todoId eq id }
                updateTodoDTO.tags.forEach { eachTagId ->
                    TodoTagTable.insert { todoTagTable ->
                        todoTagTable[todoId] = id
                        todoTagTable[tagId] = TagTable.select(TagTable.id).where { TagTable.id eq eachTagId }
                    }
                }
            }
            if (updateTodoDTO.completed != null) it.completed = updateTodoDTO.completed
            if (updateTodoDTO.dueDate != null) it.dueDate = LocalDate.parse(updateTodoDTO.dueDate)
        }
    }

    override fun replaceTodo(id: Int, replaceTodoDTO: ReplaceTodoDTO): TodoDAO? = transaction {
        TodoDAO.findByIdAndUpdate(id) {
            it.title = replaceTodoDTO.title
            it.description = replaceTodoDTO.description
            TodoTagTable.deleteWhere { TodoTagTable.todoId eq id }
            replaceTodoDTO.tags.forEach { eachTagId ->
                TodoTagTable.insert { todoTagTable ->
                    todoTagTable[todoId] = id
                    todoTagTable[tagId] = TagTable.select(TagTable.id).where { TagTable.id eq eachTagId }
                }
            }
            it.completed = replaceTodoDTO.completed
            it.dueDate = LocalDate.parse(replaceTodoDTO.dueDate)
        }
    }

    override fun deleteTodo(id: Int) = transaction {
        TodoDAO.findById(id)!!.delete()
    }

}