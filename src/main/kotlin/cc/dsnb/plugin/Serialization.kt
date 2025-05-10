package cc.dsnb.plugin

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                // If remove this line, the @SerialName annotation will not work
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                // If remove this line, the DTOs will contain a key named "type" and value is the class name
                classDiscriminatorMode = ClassDiscriminatorMode.NONE
            }
        )
    }
}