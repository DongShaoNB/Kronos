package cc.dsnb.model.dto.setting

import kotlinx.serialization.Serializable

@Serializable
data class NewSettingDTO(
    val key: String,
    val value: String
)