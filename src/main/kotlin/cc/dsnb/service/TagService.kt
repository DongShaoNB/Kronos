package cc.dsnb.service

import cc.dsnb.model.dao.TagDAO
import cc.dsnb.model.dto.tag.ReplaceTagDTO
import cc.dsnb.model.dto.tag.UpdateTagDTO


interface TagService {

    fun createTag(name: String, description: String?, userId: Int): TagDAO
    fun findTagById(id: Int): TagDAO?
    fun findTagByName(name: String): List<TagDAO>
    fun findAllTags(): List<TagDAO>
    fun updateTag(id: Int, updateTagDTO: UpdateTagDTO): TagDAO?
    fun replaceTag(id: Int, replaceTagDTO: ReplaceTagDTO): TagDAO?
    fun deleteTag(id: Int)

}