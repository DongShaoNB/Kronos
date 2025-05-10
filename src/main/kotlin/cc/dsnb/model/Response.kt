package cc.dsnb.model

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val code: Int,
    val message: String? = null,
    val data: T
)