package services

import models.SerVivo
import models.Organo
import utils.FileManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Servicio para gestionar las operaciones CRUD de seres vivos con persistencia en archivo.
 */
class SerVivoService {
    private val filePath = "src/main/resources/data/seres_vivos.txt" // Ruta al archivo de datos.
    private val seresVivos = mutableListOf<SerVivo>()                // Lista mutable para almacenar los seres vivos.

    init {
        loadFromFile() // Carga los datos desde el archivo al iniciar la clase.
    }

    // Crea un nuevo ser vivo y guarda los cambios en el archivo.
    fun create(serVivo: SerVivo) {
        seresVivos.add(serVivo)
        saveToFile()
    }

    // Devuelve la lista de seres vivos.
    fun read(): List<SerVivo> = seresVivos

    // Actualiza un ser vivo existente por ID y guarda los cambios.
    fun update(id: Int, updatedSerVivo: SerVivo) {
        val index = seresVivos.indexOfFirst { it.id == id }
        if (index != -1) {
            seresVivos[index] = updatedSerVivo
            saveToFile()
        } else {
            println("Ser Vivo no encontrado.")
        }
    }

    // Elimina un ser vivo por ID y guarda los cambios.
    fun delete(id: Int) {
        seresVivos.removeIf { it.id == id }
        saveToFile()
    }

    // Carga los datos de los seres vivos desde el archivo.
    private fun loadFromFile() {
        val data = FileManager.read(filePath)

        data.forEach { line ->
            val parts = line.split(",") // Divide la línea en partes.
            val organos = mutableListOf<Organo>()

            // Procesa los órganos si están presentes en los datos.
            if (parts.size > 5) {
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

            // Crea y añade un ser vivo a la lista.
            val serVivo = SerVivo(
                id = parts[0].toInt(),
                nombre = parts[1],
                tipo = parts[2],
                esVertebrado = parts[3].toBoolean(),
                fechaNacimiento = LocalDate.parse(parts[4]),
                organos = organos
            )
            seresVivos.add(serVivo)
        }
    }

    // Guarda los datos actuales de los seres vivos en el archivo.
    private fun saveToFile() {
        val data = seresVivos.joinToString("\n") { serVivo ->
            val organosStr = if (serVivo.organos.isNotEmpty()) {
                "[${serVivo.organos.joinToString(";") { organo ->
                    "${organo.id}:${organo.nombre}:${organo.funcion}:${organo.cantidadCelulas}:${organo.eficiencia}"
                }}]"
            } else {
                "[]"
            }
            "${serVivo.id},${serVivo.nombre},${serVivo.tipo},${serVivo.esVertebrado},${serVivo.fechaNacimiento},$organosStr"
        }
        FileManager.write(filePath, data)
    }
}
