package cc.dsnb.service.impl

import cc.dsnb.database.table.RoleTable
import cc.dsnb.model.dao.RoleDAO
import cc.dsnb.model.dto.role.ReplaceRoleDTO
import cc.dsnb.model.dto.role.UpdateRoleDTO
import cc.dsnb.service.RoleService
import org.jetbrains.exposed.sql.transactions.transaction

class RoleServiceImpl : RoleService {

    override fun createRole(
        name: String,
        description: String?,
        isAdmin: Boolean?
    ): RoleDAO = transaction {
        RoleDAO.new {
            this.name = name
            this.description = description
            this.isAdmin = isAdmin == true
        }
    }

    override fun findRoleById(id: Int): RoleDAO? = transaction { RoleDAO.findById(id) }

    override fun findRoleByName(name: String): RoleDAO? =
        transaction { RoleDAO.find { RoleTable.name eq name }.firstOrNull() }

    override fun findAllRoles(): List<RoleDAO> = transaction { RoleDAO.all().toList() }

    override fun updateRole(id: Int, updateRoleDTO: UpdateRoleDTO): RoleDAO? = transaction {
        RoleDAO.findByIdAndUpdate(id) {
            if (updateRoleDTO.name != null) it.name = updateRoleDTO.name
            if (updateRoleDTO.description != null) it.description = updateRoleDTO.description
        }
    }

    override fun replaceRole(id: Int, replaceRoleDTO: ReplaceRoleDTO): RoleDAO? = transaction {
        RoleDAO.findByIdAndUpdate(id) {
            it.name = replaceRoleDTO.name
            it.description = replaceRoleDTO.description
        }
    }

    override fun deleteRole(id: Int) = transaction {
        RoleDAO.findById(id)!!.delete()
    }

}