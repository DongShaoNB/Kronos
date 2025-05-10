package cc.dsnb.route

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.todo.NewTodoDTO
import cc.dsnb.model.dto.todo.ReplaceTodoDTO
import cc.dsnb.model.dto.todo.UpdateTodoDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runWithUserId
import cc.dsnb.service.TodoService
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun Route.todoRouting() {

    val todoService by application.inject<TodoService>()

    route("todos") {

        post {
            call.runWithUserId { userId ->
                try {
                    val newTodoDTO = call.receive<NewTodoDTO>()
                    if (todoService.findTodoByTitle(newTodoDTO.title)
                            .any { it.userId.value == userId }
                    ) return@post call.respond(ResponseCode.TODO_TITLE_IN_USE)
                    val dueDate = if (!newTodoDTO.dueDate.isNullOrBlank()) LocalDate.parse(newTodoDTO.dueDate) else null
                    val todoDTO = todoService.createTodo(
                        newTodoDTO.title,
                        newTodoDTO.description,
                        newTodoDTO.tags,
                        dueDate,
                        userId
                    )
                        .toDTO()
                    call.respond(ResponseCode.CREATED, todoDTO)
                } catch (_: DateTimeParseException) {
                    call.respond(ResponseCode.TODO_DUE_TIME_FORMAT_INVALID)
                }
            }
        }

        get {
            call.runWithUserId { userId ->
                val title = call.queryParameters["title"]
                val description = call.queryParameters["description"]
                val completed = call.queryParameters["completed"]?.toBoolean()
                val todoDTO = todoService.findAllTodos()
                    .filter {
                        (title.isNullOrBlank() || it.title.contains(title, true)) &&
                                (description.isNullOrBlank() || it.description?.contains(description, true) == true) &&
                                (it.userId.value.toString().contains(userId.toString(), true)) &&
                                (completed == null || it.completed == completed)
                    }
                    .map { it.toDTO() }
                call.respond(ResponseCode.OK, todoDTO)
            }
        }

        route("{id}") {

            get {
                call.runWithUserId { userId ->
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                    if (todoService.findTodoById(id)?.userId?.value != userId) return@get call.respond(ResponseCode.NO_PERMISSION)
                    val todoDTO =
                        todoService.findTodoById(id)?.toDTO() ?: return@get call.respond(ResponseCode.TODO_NOT_FOUND)
                    call.respond(ResponseCode.OK, todoDTO)
                }
            }

            patch {
                call.runWithUserId { userId ->
                    try {
                        val id =
                            call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(ResponseCode.INVALID_ID)
                        if (todoService.findTodoById(id)?.userId?.value != userId) return@patch call.respond(
                            ResponseCode.NO_PERMISSION
                        )
                        val updateTodoDTO = call.receive<UpdateTodoDTO>()
                        // Check if the due date is valid, TodoService#updateTodo will force String to LocalDateTime
                        if (updateTodoDTO.dueDate != null) LocalDate.parse(updateTodoDTO.dueDate)
                        val todoDTO = todoService.updateTodo(id, updateTodoDTO)?.toDTO()
                            ?: return@patch call.respond(ResponseCode.TODO_NOT_FOUND)
                        call.respond(ResponseCode.OK, todoDTO)
                    } catch (_: DateTimeParseException) {
                        call.respond(ResponseCode.TODO_DUE_TIME_FORMAT_INVALID)
                    }
                }
            }

            put {
                call.runWithUserId { userId ->
                    try {
                        val id =
                            call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(ResponseCode.INVALID_ID)
                        if (todoService.findTodoById(id)?.userId?.value != userId) return@put call.respond(ResponseCode.NO_PERMISSION)
                        val replaceTodoDTO = call.receive<ReplaceTodoDTO>()
                        // Check if the due date is valid, TodoService#replaceTodo will force String to LocalDate
                        LocalDate.parse(replaceTodoDTO.dueDate)
                        val todoDTO = todoService.replaceTodo(id, replaceTodoDTO)?.toDTO()
                            ?: return@put call.respond(ResponseCode.TODO_NOT_FOUND)
                        call.respond(ResponseCode.OK, todoDTO)
                    } catch (_: DateTimeParseException) {
                        call.respond(ResponseCode.TODO_DUE_TIME_FORMAT_INVALID)
                    }
                }
            }

            delete {
                call.runWithUserId { userId ->
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(ResponseCode.INVALID_ID)
                    if (todoService.findTodoById(id)?.userId?.value != userId) return@delete call.respond(ResponseCode.NO_PERMISSION)
                    todoService.findTodoById(id) ?: return@delete call.respond(ResponseCode.TODO_NOT_FOUND)
                    todoService.deleteTodo(id)
                    call.respond(ResponseCode.NO_CONTENT)
                }
            }

        }

    }

}