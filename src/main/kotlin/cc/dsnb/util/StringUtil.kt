package cc.dsnb.util

object StringUtil {

    fun isValidUsername(username: String): Boolean {
        val usernameRegex = Regex("""^[a-z][a-z0-9]*$""")
        return usernameRegex.matches(username) && username.length in 3..20
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
        return emailRegex.matches(email)
    }

}