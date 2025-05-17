package cc.dsnb.route

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.AIPromptDTO
import cc.dsnb.model.dto.todo.NewTodoDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runWithUserId
import cc.dsnb.service.AIService
import cc.dsnb.service.TodoService
import cc.dsnb.util.StringUtil
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun Route.aiRouting() {

    val aiService by application.inject<AIService>()
    val todoService by application.inject<TodoService>()

    route("ai") {

        route("todos") {

            post {
                call.runWithUserId { userId ->
                    val aiPromptDTO = call.receive<AIPromptDTO>()
                    val newTodoDTO =
                        StringUtil.json.decodeFromString<NewTodoDTO>(aiService.chatToAi(aiPromptDTO.prompt)!!)
                    runCatching {
                        val dueDate =
                            if (!newTodoDTO.dueDate.isNullOrBlank()) LocalDate.parse(newTodoDTO.dueDate) else null
                        todoService.createTodo(
                            newTodoDTO.title,
                            newTodoDTO.description,
                            newTodoDTO.tags,
                            dueDate,
                            userId
                        )
                        call.respond(ResponseCode.OK, newTodoDTO)
                    }.onFailure { exception ->
                        when (exception) {
                            is DateTimeParseException -> {
                                call.respond(ResponseCode.TODO_DUE_TIME_FORMAT_INVALID)
                            }
                        }
                    }
                }
            }

        }

    }

}