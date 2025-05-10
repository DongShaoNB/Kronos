package cc.dsnb.database.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TodoTagTable : Table("todo_tags") {

    val todoId = reference("todo_id", TodoTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = reference("tag_id", TagTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(todoId, tagId)

}