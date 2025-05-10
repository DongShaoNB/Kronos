package cc.dsnb.service

import cc.dsnb.model.dao.SettingDAO
import cc.dsnb.model.dto.setting.UpdateSettingDTO

interface SystemService {

    fun createSetting(key: String, value: String): SettingDAO
    fun findSettingById(id: Int): SettingDAO?
    fun findSettingByKey(key: String): SettingDAO?
    fun findAllSettings(): List<SettingDAO>
    fun updateSetting(id: Int, updateSettingDTO: UpdateSettingDTO): SettingDAO?

}