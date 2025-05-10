package cc.dsnb.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UserTable : IntIdTable("users") {

    val username = varchar("username", 50).uniqueIndex()

    // TODO
    val avatar = varchar("avatar", 255).default("default/default.png")
    val name = varchar("name", 50)
    val email = varchar("email", 255).uniqueIndex()
    val emailVerified = bool("email_verified").default(false)
    val roleId =
        reference("role_id", RoleTable.id, onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.RESTRICT)
    val language = varchar("language", 2).default("zh")
    val passwordHash = varchar("password_hash", 60)
    val registerIp = varchar("register_ip", 15)
    val registeredAt = datetime("registered_at").defaultExpression(CurrentDateTime)
    val lastLoginIp = varchar("last_login_ip", 15).nullable()
    val lastLoginAt = datetime("last_login_at").defaultExpression(CurrentDateTime)

}