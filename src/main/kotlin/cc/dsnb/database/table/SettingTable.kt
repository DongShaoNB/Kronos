package cc.dsnb.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object SettingTable : IntIdTable("settings") {

    val key = varchar("key", 255).uniqueIndex()
    val value = text("value")

}