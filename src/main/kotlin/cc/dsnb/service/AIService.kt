package cc.dsnb.service

interface AIService {

    suspend fun chatToAi(prompt: String): String?

}
