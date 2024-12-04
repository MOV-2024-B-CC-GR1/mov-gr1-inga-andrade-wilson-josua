package services

import models.SerVivo
import models.Organo
import utils.FileManager

class SerVivoService {
    private val filePath = "src/main/resources/data/seres_vivos.txt"
    private val seresVivos = mutableListOf<SerVivo>()

    init {
        loadFromFile()
    }

    fun create(serVivo: SerVivo) {
        seresVivos.add(serVivo)
        saveToFile()
    }

    fun read(): List<SerVivo> = seresVivos

    fun update(id: Int, updatedSerVivo: SerVivo) {
        val index = seresVivos.indexOfFirst { it.id == id }
        if (index != -1) {
            seresVivos[index] = updatedSerVivo
            saveToFile()
        } else {
            println("Ser Vivo no encontrado.")
        }
    }

    fun delete(id: Int) {
        seresVivos.removeIf { it.id == id }
        saveToFile()
    }

    private fun loadFromFile() {
        val data = FileManager.read(filePath)
        data.forEach { line ->
            val parts = line.split(",")
            val organos = mutableListOf<Organo>()
            if (parts.size > 5) { // Si hay órganos en la línea
                val organosData = parts[5].removeSurrounding("[", "]").split(";")
                organosData.forEach { organoData ->
                    val organoParts = organoData.split(":")
                    if (organoParts.size == 5) {
                        val organo = Organo(
                            id = organoParts[0].toInt(),
                            nombre = organoParts[1],
                            funcion = organoParts[2],
                            cantidadCelulas = organoParts[3].toInt(),
                            eficiencia = organoParts[4].toDouble()
                        )
                        organos.add(organo)
                    }
                }
            }

            val serVivo = SerVivo(
                id = parts[0].toInt(),
                nombre = parts[1],
                tipo = parts[2],
                esVertebrado = parts[3].toBoolean(),
                edadSerVivo = parts[4].toInt(),
                organos = organos
            )
            seresVivos.add(serVivo)
        }
    }

    private fun saveToFile() {
        val data = seresVivos.joinToString("\n") { serVivo ->
            val organosStr = if (serVivo.organos.isNotEmpty()) {
                // Aseguramos que los órganos estén formateados correctamente
                "[${serVivo.organos.joinToString(";") { organo ->
                    "${organo.id}:${organo.nombre}:${organo.funcion}:${organo.cantidadCelulas}:${organo.eficiencia}"
                }}]"
            } else {
                "[]"
            }
            // Guardamos el ser vivo con sus órganos formateados
            "${serVivo.id},${serVivo.nombre},${serVivo.tipo},${serVivo.esVertebrado},${serVivo.edadSerVivo},$organosStr"
        }
        FileManager.write(filePath, data)
    }
}
