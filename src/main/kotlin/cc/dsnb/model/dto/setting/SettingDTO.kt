package cc.dsnb.model.dto.setting

import kotlinx.serialization.Serializable

@Serializable
data class SettingDTO(
    val id: Int,
    val key: String,
    val value: String
)