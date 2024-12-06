package utils

import java.io.File

object FileManager {

    // Metodo para leer un archivo, y si no existe lo crea
    fun read(filePath: String): List<String> {
        val file = File(filePath)
        if (!file.exists()) {
            // Si el archivo no existe, lo creamos
            file.createNewFile()
        }
        return file.readLines() // Retornamos las líneas del archivo
    }

    // Metodo para escribir en un archivo
    fun write(filePath: String, content: String) {
        val file = File(filePath)
        if (!file.exists()) {
            // Si el archivo no existe, lo creamos
            file.createNewFile()
        }
        file.writeText(content) // Escribimos el contenido en el archivo
    }

    // Metodo para asegurarse de que ambos archivos existen
    fun ensureFilesExist() {
        val seresVivosFile = File("src/main/resources/data/seres_vivos.txt")
        val organosFile = File("src/main/resources/data/organos.txt")

        // Si no existen, los creamos
        if (!seresVivosFile.exists()) {
            seresVivosFile.createNewFile()
            println("Archivo 'seres_vivos.txt' creado.")
        }

        if (!organosFile.exists()) {
            organosFile.createNewFile()
            println("Archivo 'organos.txt' creado.")
        }
    }
}
