package cc.dsnb.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

object StringUtil {

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        // If remove this line, the @SerialName annotation will not work
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        // If remove this line, the DTOs will contain a key named "type" and value is the class name
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    fun isValidUsername(username: String): Boolean {
        val usernameRegex = Regex("""^[a-z][a-z0-9]*$""")
        return usernameRegex.matches(username) && username.length in 3..20
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
        return emailRegex.matches(email)
    }

}