package cc.dsnb.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object RoleTable : IntIdTable("roles") {

    val name = varchar("name", 50).uniqueIndex()
    val description = text("description").nullable()
    val isAdmin = bool("is_admin").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

}