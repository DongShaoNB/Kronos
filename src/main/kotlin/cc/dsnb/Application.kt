package cc.dsnb

import cc.dsnb.plugin.*
import cc.dsnb.util.AIUtil
import cc.dsnb.util.RedisUtil
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureAuth()
    configureCORS()
    configureDatabase()
    configureInjection()
    configureRateLimit()
    configureRouting()
    configureSerialization()
    configureStatusPages()
    AIUtil.init(environment.config)
    // Subscribe ApplicationStopping event to close Redis connection and shutdown Redis client
    monitor.subscribe(ApplicationStopping) {
        RedisUtil.connection.close()
        RedisUtil.client.shutdown()
    }
}
