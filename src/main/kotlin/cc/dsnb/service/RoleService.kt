package cc.dsnb.service

import cc.dsnb.model.dao.RoleDAO
import cc.dsnb.model.dto.role.ReplaceRoleDTO
import cc.dsnb.model.dto.role.UpdateRoleDTO

interface RoleService {

    fun createRole(name: String, description: String?, isAdmin: Boolean?): RoleDAO
    fun findRoleById(id: Int): RoleDAO?
    fun findRoleByName(name: String): RoleDAO?
    fun findAllRoles(): List<RoleDAO>
    fun updateRole(id: Int, updateRoleDTO: UpdateRoleDTO): RoleDAO?
    fun replaceRole(id: Int, replaceRoleDTO: ReplaceRoleDTO): RoleDAO?
    fun deleteRole(id: Int)

}