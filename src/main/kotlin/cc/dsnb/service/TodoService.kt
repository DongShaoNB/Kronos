package cc.dsnb.service

import cc.dsnb.model.dao.TodoDAO
import cc.dsnb.model.dto.todo.ReplaceTodoDTO
import cc.dsnb.model.dto.todo.UpdateTodoDTO
import java.time.LocalDate


interface TodoService {

    fun createTodo(title: String, description: String?, tags: List<Int>?, dueDate: LocalDate?, userId: Int): TodoDAO
    fun findTodoById(id: Int): TodoDAO?
    fun findTodoByTitle(title: String): List<TodoDAO>
    fun findAllTodos(): List<TodoDAO>
    fun updateTodo(id: Int, updateTodoDTO: UpdateTodoDTO): TodoDAO?
    fun replaceTodo(id: Int, replaceTodoDTO: ReplaceTodoDTO): TodoDAO?
    fun deleteTodo(id: Int)

}