package cc.dsnb.route

import cc.dsnb.model.ResponseCode
import cc.dsnb.model.dto.tag.NewTagDTO
import cc.dsnb.model.dto.tag.ReplaceTagDTO
import cc.dsnb.model.dto.tag.UpdateTagDTO
import cc.dsnb.plugin.respond
import cc.dsnb.plugin.runWithUserId
import cc.dsnb.service.TagService
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.tagRouting() {

    val tagService by application.inject<TagService>()

    route("tags") {

        post {
            call.runWithUserId { userId ->
                val newTagDTO = call.receive<NewTagDTO>()
                if (tagService.findTagByName(newTagDTO.name)
                        .any { it.userId.value == userId }
                ) return@post call.respond(ResponseCode.TAG_NAME_IN_USE)
                val tagDTO = tagService.createTag(newTagDTO.name, newTagDTO.description, userId).toDTO()
                call.respond(ResponseCode.CREATED, tagDTO)
            }
        }

        get {
            call.runWithUserId { userId ->
                val name = call.queryParameters["name"]
                val description = call.queryParameters["description"]
                val tagDTO = tagService.findAllTags().filter {
                    (name.isNullOrBlank() || it.name.contains(name, true)) &&
                            (description.isNullOrBlank() || it.description?.contains(description, true) == true) &&
                            (it.userId.value == userId)
                }.map { it.toDTO() }
                call.respond(ResponseCode.OK, tagDTO)
            }
        }

        route("{id}") {

            get {
                call.runWithUserId { userId ->
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(ResponseCode.INVALID_ID)
                    if (tagService.findTagById(id)?.userId?.value != userId) return@get call.respond(ResponseCode.NO_PERMISSION)
                    val tagDTO = tagService.findTagById(id)?.toDTO()
                        ?: return@get call.respond(ResponseCode.TAG_NOT_FOUND)
                    call.respond(ResponseCode.OK, tagDTO)
                }
            }

            patch {
                call.runWithUserId { userId ->
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@patch call.respond(ResponseCode.INVALID_ID)
                    if (tagService.findTagById(id)?.userId?.value != userId) return@patch call.respond(ResponseCode.NO_PERMISSION)
                    val updateTagDTO = call.receive<UpdateTagDTO>()
                    val tagDTO = tagService.updateTag(id, updateTagDTO)?.toDTO()
                        ?: return@patch call.respond(ResponseCode.TAG_NOT_FOUND)
                    call.respond(ResponseCode.OK, tagDTO)
                }
            }

            put {
                call.runWithUserId { userId ->
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(ResponseCode.INVALID_ID)
                    if (tagService.findTagById(id)?.userId?.value != userId) return@put call.respond(ResponseCode.NO_PERMISSION)
                    val replaceTagDTO = call.receive<ReplaceTagDTO>()
                    val tagDTO = tagService.replaceTag(id, replaceTagDTO)?.toDTO() ?: return@put call.respond(
                        ResponseCode.TAG_NOT_FOUND
                    )
                    call.respond(ResponseCode.OK, tagDTO)
                }
            }

            delete {
                call.runWithUserId { userId ->
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(ResponseCode.INVALID_ID)
                    if (tagService.findTagById(id)?.userId?.value != userId) return@delete call.respond(ResponseCode.NO_PERMISSION)
                    tagService.findTagById(id) ?: return@delete call.respond(ResponseCode.TAG_NOT_FOUND)
                    tagService.deleteTag(id)
                    call.respond(ResponseCode.NO_CONTENT)
                }
            }

        }

    }

}