package cc.dsnb.util

import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.ktor.server.config.*

object AIUtil {

    lateinit var model: String
    var temperature: Double = 0.2
    lateinit var openAI: OpenAI

    fun init(config: ApplicationConfig) {
        val openAIHost = OpenAIHost(config.property("openai.base_url").getString())
        val openAIConfig = OpenAIConfig(
            token = config.property("openai.token").getString(),
            host = openAIHost
        )
        model = config.property("openai.model").getString()
        temperature = config.property("openai.temperature").getString().toDouble()
        openAI = OpenAI(openAIConfig)
    }

}