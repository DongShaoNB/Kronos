package cc.dsnb.route.admin

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.tag.NewTagDTO
import cc.dsnb.model.dto.tag.ReplaceTagDTO
import cc.dsnb.model.dto.tag.UpdateTagDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runIfAdmin
import cc.dsnb.service.TagService
import cc.dsnb.service.UserService
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminTagRouting() {

    val tagService by application.inject<TagService>()
    val userService by application.inject<UserService>()

    route("tags") {

        post {
            call.runIfAdmin {
                val newTagDTO = call.receive<NewTagDTO>()
                if (newTagDTO.userId == null) return@post call.respond(ResponseCode.REQUIRED_USER_ID)
                if (userService.findUserById(newTagDTO.userId) == null) return@post call.respond(ResponseCode.USER_NOT_FOUND)
                val tagDTO = tagService.createTag(newTagDTO.name, newTagDTO.description, newTagDTO.userId).toDTO()
                call.respond(ResponseCode.CREATED, tagDTO)
            }
        }

        get {
            call.runIfAdmin {
                val name = call.queryParameters["name"]
                val description = call.queryParameters["description"]
                val userId = call.queryParameters["userId"]
                val tagDTO = tagService.findAllTags()
                    .filter {
                        (name.isNullOrBlank() || it.name.contains(
                            name,
                            true
                        )) && (description.isNullOrBlank() || it.description?.contains(
                            description, true
                        ) == true) && (userId == null || it.userId.value.toString().contains(userId))
                    }
                    .map { it.toDTO() }
                call.respond(ResponseCode.OK, tagDTO)
            }
        }

        route("{id}") {

            get {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                    val tagDTO =
                        tagService.findTagById(id)?.toDTO() ?: return@get call.respond(ResponseCode.TAG_NOT_FOUND)
                    call.respond(ResponseCode.OK, tagDTO)
                }
            }

            patch {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(ResponseCode.INVALID_ID)
                    val updateTagDTO = call.receive<UpdateTagDTO>()
                    val tagDTO = tagService.updateTag(id, updateTagDTO)?.toDTO() ?: return@patch call.respond(
                        ResponseCode.TAG_NOT_FOUND
                    )
                    call.respond(ResponseCode.OK, tagDTO)
                }
            }

            put {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(ResponseCode.INVALID_ID)
                    val replaceTagDTO = call.receive<ReplaceTagDTO>()
                    val tagDTO = tagService.replaceTag(id, replaceTagDTO)?.toDTO() ?: return@put call.respond(
                        ResponseCode.TAG_NOT_FOUND
                    )
                    call.respond(ResponseCode.OK, tagDTO)
                }
            }

            delete {
                call.runIfAdmin {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(ResponseCode.INVALID_ID)
                    tagService.findTagById(id) ?: return@delete call.respond(ResponseCode.TAG_NOT_FOUND)
                    tagService.deleteTag(id)
                    call.respond(ResponseCode.NO_CONTENT)
                }
            }

        }

    }

}