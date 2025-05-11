package cc.dsnb.service.impl

import at.favre.lib.crypto.bcrypt.BCrypt
import cc.dsnb.database.table.UserTable
import cc.dsnb.model.dao.RoleDAO
import cc.dsnb.model.dao.UserDAO
import cc.dsnb.model.dto.user.AdminUpdateUserDTO
import cc.dsnb.model.dto.user.CommonUpdateUserDTO
import cc.dsnb.model.dto.user.ReplaceUserDTO
import cc.dsnb.service.UserService
import cc.dsnb.util.EmailUtil
import cc.dsnb.util.RedisUtil
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import jakarta.mail.Message
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

class UserServiceImpl : UserService {
    override fun createUser(
        username: String,
        avatar: String?,
        name: String,
        email: String,
        roleId: Int,
        language: String?,
        password: String,
        registerIp: String
    ): UserDAO {
        val userDAO = transaction {
            UserDAO.new {
                this.username = username
                if (avatar != null) this.avatar = avatar
                this.name = name
                this.email = email
                if (language != null) this.language = language
                this.passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
                this.registerIp = registerIp
                this.roleId = RoleDAO.findById(roleId)!!.id
            }
        }
        return userDAO
    }

    override fun findUserById(id: Int): UserDAO? = transaction { UserDAO.findById(id) }

    override fun findUserByUsername(username: String): UserDAO? = transaction {
        UserDAO.find { UserTable.username eq username }.firstOrNull()
    }

    override fun findUserByEmail(email: String): UserDAO? = transaction {
        UserDAO.find { UserTable.email eq email }.firstOrNull()
    }

    override fun findAllUsers(): List<UserDAO> = transaction { UserDAO.all().toList() }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun updateUser(id: Int, commonUpdateUserDTO: CommonUpdateUserDTO): UserDAO? {
        if (commonUpdateUserDTO.email != null) {
            RedisUtil.commands.del("user.$id.email-verification-code")
        }
        return transaction {
            UserDAO.findByIdAndUpdate(id) {
                if (commonUpdateUserDTO.name != null) it.name = commonUpdateUserDTO.name
                if (commonUpdateUserDTO.email != null) {
                    it.email = commonUpdateUserDTO.email
                    it.emailVerified = false
                }
                if (commonUpdateUserDTO.language != null) it.language = commonUpdateUserDTO.language
            }
        }
    }

    override fun updateUser(id: Int, adminUpdateUserDTO: AdminUpdateUserDTO): UserDAO? = transaction {
        UserDAO.findByIdAndUpdate(id) {
            if (adminUpdateUserDTO.avatar != null) it.avatar = adminUpdateUserDTO.avatar
            if (adminUpdateUserDTO.name != null) it.name = adminUpdateUserDTO.name
            if (adminUpdateUserDTO.email != null) {
                it.email = adminUpdateUserDTO.email
                it.emailVerified = false
            }
            if (adminUpdateUserDTO.emailVerified != null) it.emailVerified = adminUpdateUserDTO.emailVerified
            if (adminUpdateUserDTO.language != null) it.language = adminUpdateUserDTO.language
        }
    }

    override fun replaceUser(id: Int, replaceUserDTO: ReplaceUserDTO): UserDAO? = transaction {
        UserDAO.findByIdAndUpdate(id) {
            it.avatar = replaceUserDTO.avatar
            it.name = replaceUserDTO.name
            it.email = replaceUserDTO.email
            it.emailVerified = replaceUserDTO.emailVerified
            it.language = replaceUserDTO.language
        }
    }

    override fun deleteUser(id: Int) = transaction {
        UserDAO.findById(id)!!.delete()
    }

    override fun verifyUserPassword(id: Int, password: String): Boolean = transaction {
        BCrypt.verifyer().verify(password.toCharArray(), UserDAO.findById(id)!!.passwordHash).verified
    }

    override fun updateUserPassword(id: Int, password: String) {
        transaction {
            UserDAO.findByIdAndUpdate(id) {
                it.passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
            }
        }
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun sendVerificationEmail(id: Int): Boolean {
        val code = Random.nextInt(100000, 999999).toString()
        val message = MimeMessage(EmailUtil.session).apply {
            setFrom(InternetAddress(EmailUtil.email))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(findUserById(id)!!.email))
            subject = "Kronos 验证码"
            setText("您正在通过 Kronos 验证该邮箱，您的验证码是 [$code]，有效期 [5分钟]。如非本人操作，请忽略此邮件。")
        }
        runCatching {
            Transport.send(message)
            RedisUtil.commands.set("user.$id.email-verification-code", code)
            RedisUtil.commands.expire("user.$id.email-verification-code", 300)
            return true
        }
        return false
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun verifyUserEmail(id: Int, code: String): Boolean {
        return if (RedisUtil.commands.get("user.$id.email-verification-code") == code) {
            RedisUtil.commands.del("user.$id.email-verification-code")
            transaction {
                UserDAO.findByIdAndUpdate(id) {
                    it.emailVerified = true
                }
            }
            true
        } else {
            false
        }
    }

}