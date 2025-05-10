package cc.dsnb.service

import cc.dsnb.database.table.TagTable
import cc.dsnb.model.dao.TagDAO
import cc.dsnb.model.dao.UserDAO
import cc.dsnb.model.dto.tag.ReplaceTagDTO
import cc.dsnb.model.dto.tag.UpdateTagDTO
import org.jetbrains.exposed.sql.transactions.transaction

class TagServiceImpl : TagService {

    override fun createTag(
        name: String,
        description: String?,
        userId: Int
    ): TagDAO = transaction {
        TagDAO.new {
            this.name = name
            this.description = description
            this.userId = UserDAO.findById(userId)!!.id
        }
    }

    override fun findTagById(id: Int): TagDAO? = transaction { TagDAO.findById(id) }

    override fun findTagByName(name: String): List<TagDAO> =
        transaction { TagDAO.find { TagTable.name eq name }.toList() }

    override fun findAllTags(): List<TagDAO> = transaction { TagDAO.all().toList() }

    override fun updateTag(id: Int, updateTagDTO: UpdateTagDTO): TagDAO? = transaction {
        TagDAO.findByIdAndUpdate(id) {
            if (updateTagDTO.name != null) it.name = updateTagDTO.name
            if (updateTagDTO.description != null) it.description = updateTagDTO.description
        }
    }

    override fun replaceTag(id: Int, replaceTagDTO: ReplaceTagDTO): TagDAO? = transaction {
        TagDAO.findByIdAndUpdate(id) {
            it.name = replaceTagDTO.name
            it.description = replaceTagDTO.description
        }
    }

    override fun deleteTag(id: Int) = transaction {
        TagDAO.findById(id)!!.delete()
    }

}