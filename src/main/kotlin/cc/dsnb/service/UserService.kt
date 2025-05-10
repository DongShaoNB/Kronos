package cc.dsnb.service

import cc.dsnb.model.dao.UserDAO
import cc.dsnb.model.dto.user.AdminUpdateUserDTO
import cc.dsnb.model.dto.user.CommonUpdateUserDTO
import cc.dsnb.model.dto.user.ReplaceUserDTO

interface UserService {

    fun createUser(
        username: String,
        avatar: String?,
        name: String,
        email: String,
        roleId: Int,
        language: String?,
        password: String,
        registerIp: String
    ): UserDAO

    fun findUserById(id: Int): UserDAO?
    fun findUserByUsername(username: String): UserDAO?
    fun findUserByEmail(email: String): UserDAO?
    fun findAllUsers(): List<UserDAO>
    suspend fun updateUser(id: Int, commonUpdateUserDTO: CommonUpdateUserDTO): UserDAO?
    fun updateUser(id: Int, adminUpdateUserDTO: AdminUpdateUserDTO): UserDAO?
    fun replaceUser(id: Int, replaceUserDTO: ReplaceUserDTO): UserDAO?
    fun deleteUser(id: Int)
    fun verifyUserPassword(id: Int, password: String): Boolean
    fun updateUserPassword(id: Int, password: String)
    suspend fun sendVerificationEmail(id: Int): Boolean
    suspend fun verifyUserEmail(id: Int, code: String): Boolean

}