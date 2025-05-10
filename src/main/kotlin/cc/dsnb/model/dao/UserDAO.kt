package cc.dsnb.model.dao

import cc.dsnb.database.table.UserTable
import cc.dsnb.model.dto.user.UserDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class UserDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<UserDAO>(UserTable)

    var username by UserTable.username
    var avatar by UserTable.avatar
    var name by UserTable.name
    var email by UserTable.email
    var emailVerified by UserTable.emailVerified
    var roleId by UserTable.roleId
    var language by UserTable.language
    var passwordHash by UserTable.passwordHash
    var registerIp by UserTable.registerIp
    var registeredAt by UserTable.registeredAt
    var lastLoginIp by UserTable.lastLoginIp
    var lastLoginAt by UserTable.lastLoginAt

    fun toDTO() = transaction {
        UserDTO(
            id = this@UserDAO.id.value,
            username = username,
            avatar = avatar,
            name = name,
            email = email,
            emailVerified = emailVerified,
            roleId = roleId.value,
            language = language,
            registerIp = registerIp,
            registeredAt = registeredAt.toString(),
            lastLoginIp = lastLoginIp,
            lastLoginAt = lastLoginAt.toString()
        )
    }

}
