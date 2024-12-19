package services

import models.SerVivo
import java.io.File

class SerVivoService {
    private val seresVivos = mutableListOf<SerVivo>()

    fun create(serVivo: SerVivo) {
        seresVivos.add(serVivo)
        println("Ser vivo creado: $serVivo")
    }

    fun read(): List<SerVivo> {
        return seresVivos
    }

    fun update(id: Int, updatedSerVivo: SerVivo): Boolean {
        val index = seresVivos.indexOfFirst { it.id == id }
        return if (index != -1) {
            seresVivos[index] = updatedSerVivo
            println("Ser vivo actualizado: ${seresVivos[index]}")
            true
        } else {
            println("No se encontró un ser vivo con ID $id")
            false
        }
    }

    fun delete(id: Int): Boolean {
        val index = seresVivos.indexOfFirst { it.id == id }
        return if (index != -1) {
            seresVivos.removeAt(index)
            println("Ser vivo eliminado con ID $id")
            true
        } else {
            println("No se encontró un ser vivo con ID $id")
            false
        }
    }

    // Persistencia
    fun saveToFile(fileName: String) {
        val file = File(fileName)
        file.printWriter().use { out ->
            seresVivos.forEach { serVivo ->
                out.println("${serVivo.id},${serVivo.nombre},${serVivo.tipo},${serVivo.esVertebrado},${serVivo.promedioEsperanzaVida}")
            }
        }
    }

    fun loadFromFile(fileName: String) {
        val file = File(fileName)
        if (!file.exists()) return

        file.readLines().forEach { line ->
            val parts = line.split(",")
            val serVivo = SerVivo(
                id = parts[0].toInt(),
                nombre = parts[1],
                tipo = parts[2],
                esVertebrado = parts[3].toBoolean(),
                promedioEsperanzaVida = parts[4].toDouble()
            )
            seresVivos.add(serVivo)
        }
    }
}
