package cc.dsnb.service.impl

import cc.dsnb.database.table.TagTable
import cc.dsnb.model.dao.TagDAO
import cc.dsnb.model.dao.UserDAO
import cc.dsnb.model.dto.tag.ReplaceTagDTO
import cc.dsnb.model.dto.tag.UpdateTagDTO
import cc.dsnb.service.TagService
import org.jetbrains.exposed.sql.transactions.transaction

class TagServiceImpl : TagService {

    override fun createTag(
        name: String,
        description: String?,
        userId: Int
    ): TagDAO = transaction {
        TagDAO.Companion.new {
            this.name = name
            this.description = description
            this.userId = UserDAO.Companion.findById(userId)!!.id
        }
    }

    override fun findTagById(id: Int): TagDAO? = transaction { TagDAO.Companion.findById(id) }

    override fun findTagByName(name: String): List<TagDAO> =
        transaction { TagDAO.Companion.find { TagTable.name eq name }.toList() }

    override fun findAllTags(): List<TagDAO> = transaction { TagDAO.Companion.all().toList() }

    override fun updateTag(id: Int, updateTagDTO: UpdateTagDTO): TagDAO? = transaction {
        TagDAO.Companion.findByIdAndUpdate(id) {
            if (updateTagDTO.name != null) it.name = updateTagDTO.name
            if (updateTagDTO.description != null) it.description = updateTagDTO.description
        }
    }

    override fun replaceTag(id: Int, replaceTagDTO: ReplaceTagDTO): TagDAO? = transaction {
        TagDAO.Companion.findByIdAndUpdate(id) {
            it.name = replaceTagDTO.name
            it.description = replaceTagDTO.description
        }
    }

    override fun deleteTag(id: Int) = transaction {
        TagDAO.Companion.findById(id)!!.delete()
    }

}