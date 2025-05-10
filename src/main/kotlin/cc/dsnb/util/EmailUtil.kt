package cc.dsnb.util

import io.ktor.server.config.*
import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import java.util.*

object EmailUtil {

    lateinit var props: Properties
    lateinit var session: Session
    lateinit var email: String

    fun init(config: ApplicationConfig) {
        props = Properties().apply {
            put("mail.smtp.host", config.property("mail.smtp.host").getString())
            put("mail.smtp.port", config.property("mail.smtp.port").getString().toInt())
            put("mail.smtp.auth", config.property("mail.smtp.auth").getString().toBoolean())
            put("mail.smtp.ssl.enable", config.property("mail.smtp.ssl.enable").getString().toBoolean())
            put("mail.smtp.starttls.enable", config.property("mail.smtp.starttls.enable").getString().toBoolean())
        }
        email = config.property("mail.smtp.email").getString()
        session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(
                    config.property("mail.smtp.username").getString(),
                    config.property("mail.smtp.password").getString()
                )
        })

    }

}