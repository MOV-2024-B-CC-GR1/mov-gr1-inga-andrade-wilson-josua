package utils

import java.io.File

object FileManager {
    fun read(filePath: String): List<String> {
        val file = File(filePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.readLines()
    }

    fun write(filePath: String, content: String) {
        File(filePath).writeText(content)
    }
}
