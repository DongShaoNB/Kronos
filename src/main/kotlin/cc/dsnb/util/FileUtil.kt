package cc.dsnb.util

import java.io.File

object FileUtil {

    fun saveResource(source: String, target: File): Boolean {
        return runCatching {
            val resourceStream = this::class.java.classLoader.getResourceAsStream(source)
                ?: return false

            target.apply {
                parentFile?.takeUnless { it.exists() }?.mkdirs()
                outputStream().use { fileOut ->
                    resourceStream.use { resIn ->
                        resIn.copyTo(fileOut)
                    }
                }
            }
        }.isSuccess
    }


}