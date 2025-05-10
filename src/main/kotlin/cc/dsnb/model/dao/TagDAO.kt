package cc.dsnb.model.dao

import cc.dsnb.database.table.TagTable
import cc.dsnb.model.dto.tag.TagDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class TagDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<TagDAO>(TagTable)

    var name by TagTable.name
    var description by TagTable.description
    var userId by TagTable.userId
    var createdAt by TagTable.createdAt

    fun toDTO() = transaction {
        TagDTO(
            id = this@TagDAO.id.value,
            name = name,
            description = description,
            userId = userId.value,
            createdAt = createdAt.toString()
        )
    }

}