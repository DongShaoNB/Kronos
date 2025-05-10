package cc.dsnb.util

import io.ktor.server.config.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands

object RedisUtil {

    lateinit var client: RedisClient
    lateinit var connection: StatefulRedisConnection<String, String>

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    lateinit var commands: RedisCoroutinesCommands<String, String>

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    fun init(config: ApplicationConfig) {
        client = RedisClient.create(config.property("redis.url").getString())
        connection = client.connect()
        commands = connection.coroutines()
    }

}