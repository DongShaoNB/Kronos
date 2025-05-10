package cc.dsnb.plugin

import cc.dsnb.database.table.*
import cc.dsnb.util.RedisUtil
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val config = environment.config
    val hikariConfig = HikariConfig().apply {
        driverClassName = config.property("database.driver").getString()
        jdbcUrl = config.property("database.url").getString()
        username = config.property("database.user").getString()
        password = config.property("database.password").getString()
        maximumPoolSize = config.property("database.maxPoolSize").getString().toInt()
    }
    Database.connect(datasource = HikariDataSource(hikariConfig))
    transaction {
        SchemaUtils.create(
            RoleTable,
            UserTable,
            TagTable,
            TodoTable,
            TodoTagTable,
            SettingTable
        )
    }
    // Redis
    RedisUtil.init(config)
}