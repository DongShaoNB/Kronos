package cc.dsnb.model.dto.setting

import kotlinx.serialization.Serializable

@Serializable
data class UpdateSettingDTO(
    // Normally it is not possible to update the key
    // val key: String,
    val value: String
)
