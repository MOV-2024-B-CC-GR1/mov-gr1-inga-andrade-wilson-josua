package services

import models.Organo
import utils.FileManager

/**
 * Servicio para gestionar las operaciones CRUD de órganos, con persistencia en archivo.
 */
class OrganoService {
    private val filePath = "src/main/resources/data/organos.txt" // Ruta al archivo de datos.
    private val organos = mutableListOf<Organo>()               // Lista mutable para almacenar los órganos.

    init {
        loadFromFile() // Carga los datos desde el archivo al iniciar la clase.
    }

    // Crea un nuevo órgano y guarda los cambios en el archivo.
    fun create(organo: Organo) {
        organos.add(organo)
        saveToFile()
    }

    // Devuelve la lista de órganos.
    fun read(): List<Organo> = organos

    // Actualiza un órgano existente por ID y guarda los cambios.
    fun update(id: Int, updatedOrgano: Organo) {
        val index = organos.indexOfFirst { it.id == id }
        if (index != -1) {
            organos[index] = updatedOrgano
            saveToFile()
        } else {
            println("Órgano no encontrado.")
        }
    }

    // Elimina un órgano por ID y guarda los cambios.
    fun delete(id: Int) {
        organos.removeIf { it.id == id }
        saveToFile()
    }

    // Carga los datos de órganos desde el archivo.
    private fun loadFromFile() {
        val data = FileManager.read(filePath)
        data.forEach { line ->
            val parts = line.split(",") // Divide cada línea en partes.
            val organo = Organo(
                id = parts[0].toInt(),
                nombre = parts[1],
                funcion = parts[2],
                cantidadCelulas = parts[3].toInt(),
                eficiencia = parts[4].toDouble()
            )
            organos.add(organo)
        }
    }

    // Guarda los datos actuales de los órganos en el archivo.
    private fun saveToFile() {
        val data = organos.joinToString("\n") { organo ->
            "${organo.id},${organo.nombre},${organo.funcion},${organo.cantidadCelulas},${organo.eficiencia}"
        }
        FileManager.write(filePath, data)
    }
}
