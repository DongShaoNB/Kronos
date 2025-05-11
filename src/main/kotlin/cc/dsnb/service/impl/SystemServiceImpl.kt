package cc.dsnb.service.impl

import cc.dsnb.database.table.SettingTable
import cc.dsnb.model.dao.SettingDAO
import cc.dsnb.model.dto.setting.UpdateSettingDTO
import cc.dsnb.service.SystemService
import org.jetbrains.exposed.sql.transactions.transaction


class SystemServiceImpl : SystemService {

    override fun createSetting(key: String, value: String): SettingDAO = transaction {
        SettingDAO.new {
            this.key = key
            this.value = value
        }
    }

    override fun findSettingById(id: Int): SettingDAO? = transaction { SettingDAO.findById(id) }

    override fun findSettingByKey(key: String): SettingDAO? = transaction {
        SettingDAO.find { SettingTable.key eq key }.firstOrNull()
    }

    override fun findAllSettings(): List<SettingDAO> = transaction { SettingDAO.all().toList() }

    override fun updateSetting(id: Int, updateSettingDTO: UpdateSettingDTO): SettingDAO? = transaction {
        SettingDAO.findByIdAndUpdate(id) { it.value = updateSettingDTO.value }
    }

}