package cc.dsnb.plugin

import cc.dsnb.service.*
import cc.dsnb.service.impl.*
import io.ktor.server.application.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureInjection() {
    val serviceModule = module {
        singleOf(::SystemServiceImpl) { bind<SystemService>() }
        singleOf(::RoleServiceImpl) { bind<RoleService>() }
        singleOf(::UserServiceImpl) { bind<UserService>() }
        singleOf(::TagServiceImpl) { bind<TagService>() }
        singleOf(::TodoServiceImpl) { bind<TodoService>() }
        singleOf(::AIServiceImpl) { bind<AIService>() }
    }
    install(Koin) {
        slf4jLogger()
        modules(serviceModule)
    }
}