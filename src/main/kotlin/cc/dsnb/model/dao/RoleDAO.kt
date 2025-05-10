package cc.dsnb.model.dao

import cc.dsnb.database.table.RoleTable
import cc.dsnb.model.dto.role.RoleDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class RoleDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<RoleDAO>(RoleTable)

    var name by RoleTable.name
    var description by RoleTable.description
    var isAdmin by RoleTable.isAdmin
    var createdAt by RoleTable.createdAt

    fun toDTO() = transaction {
        RoleDTO(
            id = this@RoleDAO.id.value,
            name = name,
            description = description,
            isAdmin = isAdmin,
            createdAt = createdAt.toString()
        )
    }

}
