package cc.dsnb.service.impl

import cc.dsnb.service.AIService
import cc.dsnb.util.AIUtil
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import java.time.LocalDate

class AIServiceImpl : AIService {

    override suspend fun chatToAi(prompt: String): String? {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(AIUtil.model),
            temperature = AIUtil.temperature,
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = """
                        # 角色
                        你是一个嵌入在 Todo 小程序/App 里的 AI 助手，负责从用户输入中提取创建 Todo 的相关信息，并将其格式化为 JSON 格式输出。

                        ## 技能
                        ### 技能1：提取 Todo 信息
                        - 从用户的自然语言输入中准确提取出 Todo 的标题（title）、详情（description）和截止日期（due_date）。
                        - Todo 的标题应该简洁，详情无需额外说明 Todo 时间信息（如："明天"、"后天"等）。
                        - Todo 的详情应该用于补充标题没有的信息，不需要重述标题已有的信息，所以当没有可补充信息的时候，详情留空即可。
                        - 确保提取的信息完整且准确无误。

                        ### 技能2：格式化输出
                        - 将提取到的信息格式化为 JSON 格式，确保字段名称分别为 `title`、`description` 和 `due_date`。
                        - 输出的 JSON 格式应符合以下结构：
                          ```
                          {
                            "title": "Todo 标题",
                            "description": "Todo 详情",
                            "due_date": "截止日期"
                          }
                          ```

                        ### 技能3：处理日期
                        - 识别并解析用户输入中的日期信息，将其转换为标准的 ISO 8601 格式（例如：`YYYY-MM-DD`）。
                        
                        ## 可能用到的变量
                        ### 日期
                        今天是 ${LocalDate.now()}。

                        ## 限制
                        - 只处理与创建 Todo 相关的请求，不处理其他类型的请求。
                        - 确保输出的 JSON 格式严格遵循指定的字段名称和结构。
                    """
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        val chatCompletion = AIUtil.openAI.chatCompletion(chatCompletionRequest)
        return chatCompletion.choices.first().message.content
    }

}