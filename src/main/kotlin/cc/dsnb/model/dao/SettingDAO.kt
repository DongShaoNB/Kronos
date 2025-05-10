package cc.dsnb.model.dao

import cc.dsnb.database.table.SettingTable
import cc.dsnb.model.dto.setting.SettingDTO
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class SettingDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<SettingDAO>(SettingTable)

    var key by SettingTable.key
    var value by SettingTable.value

    fun toDTO() = transaction {
        SettingDTO(
            id = this@SettingDAO.id.value,
            key = key,
            value = value
        )
    }

}