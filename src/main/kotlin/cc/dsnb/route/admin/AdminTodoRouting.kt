package cc.dsnb.route.admin

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.todo.NewTodoDTO
import cc.dsnb.model.dto.todo.ReplaceTodoDTO
import cc.dsnb.model.dto.todo.UpdateTodoDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runIfAdmin
import cc.dsnb.service.TodoService
import cc.dsnb.service.UserService
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun Route.adminTodoRouting() {

    val todoService by application.inject<TodoService>()
    val userService by application.inject<UserService>()

    route("todos") {

        post {
            call.runIfAdmin {
                try {
                    val newTodoDTO = call.receive<NewTodoDTO>()
                    if (todoService.findTodoByTitle(newTodoDTO.title) != null) return@post call.respond(ResponseCode.TODO_TITLE_IN_USE)
                    val dueDate = if (newTodoDTO.dueDate != null) LocalDate.parse(newTodoDTO.dueDate) else null
                    if (newTodoDTO.userId == null) return@post call.respond(ResponseCode.REQUIRED_USER_ID)
                    if (userService.findUserById(newTodoDTO.userId) == null) return@post call.respond(ResponseCode.USER_NOT_FOUND)
                    val todoDTO = todoService.createTodo(
                        newTodoDTO.title,
                        newTodoDTO.description,
                        newTodoDTO.tags,
                        dueDate,
                        newTodoDTO.userId
                    )
                        .toDTO()
                    call.respond(ResponseCode.CREATED, todoDTO)
                } catch (_: DateTimeParseException) {
                    call.respond(ResponseCode.TODO_DUE_TIME_FORMAT_INVALID)
                }
            }
        }

        get {
            call.runIfAdmin {
                val title = call.queryParameters["title"]
                val description = call.queryParameters["description"]
                val userId = call.queryParameters["user_id"]
                val completed = call.queryParameters["completed"]?.toBoolean()
                val todoDTO = todoService.findAllTodos()
                    .filter {
                        (title.isNullOrBlank() || it.title.contains(title, true)) &&
                                (description.isNullOrBlank() || it.description?.contains(description, true) == true) &&
                                (userId == null || it.userId.value.toString().contains(userId)) &&
                                (completed == null || it.completed == completed)
                    }
                    .map { it.toDTO() }
                call.respond(ResponseCode.OK, todoDTO)
            }
        }

        route("{id}") {

            get {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                    val todoDTO =
                        todoService.findTodoById(id)?.toDTO() ?: return@get call.respond(ResponseCode.TODO_NOT_FOUND)
                    call.respond(ResponseCode.OK, todoDTO)
                }
            }

            patch {
                call.runIfAdmin {
                    try {
                        val id =
                            call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(ResponseCode.INVALID_ID)
                        val updateTodoDTO = call.receive<UpdateTodoDTO>()
                        // Check if the due date is valid, TodoService#updateTodo will force String to LocalDate
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
                call.runIfAdmin {
                    try {
                        val id =
                            call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(ResponseCode.INVALID_ID)
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
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(ResponseCode.INVALID_ID)
                    todoService.findTodoById(id) ?: return@delete call.respond(ResponseCode.TODO_NOT_FOUND)
                    todoService.deleteTodo(id)
                    call.respond(ResponseCode.NO_CONTENT)
                }
            }

        }

    }
}