package cc.dsnb.plugin

import cc.dsnb.service.SystemService
import cc.dsnb.service.SystemServiceImpl
import cc.dsnb.service.RoleService
import cc.dsnb.service.RoleServiceImpl
import cc.dsnb.service.UserService
import cc.dsnb.service.UserServiceImpl
import cc.dsnb.service.TagService
import cc.dsnb.service.TagServiceImpl
import cc.dsnb.service.TodoService
import cc.dsnb.service.TodoServiceImpl
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
    }
    install(Koin) {
        slf4jLogger()
        modules(serviceModule)
    }
}