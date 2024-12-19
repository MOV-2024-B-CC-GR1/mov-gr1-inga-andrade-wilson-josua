package services

import models.Organo
import utils.FileManager


class OrganoService {
    private val filePath = "src/main/resources/data/organos.txt"
    private val organos = mutableListOf<Organo>()

    init {
        loadFromFile()
    }

    fun create(organo: Organo) {
        organos.add(organo)
        saveToFile()
    }

    fun read(): List<Organo> = organos

    fun update(id: Int, updatedOrgano: Organo) {
        val index = organos.indexOfFirst { it.id == id }
        if (index != -1) {
            organos[index] = updatedOrgano
            saveToFile()
        } else {
            println("Órgano no encontrado.")
        }
    }

    fun delete(id: Int) {
        organos.removeIf { it.id == id }
        saveToFile()
    }

    private fun loadFromFile() {
        val data = FileManager.read(filePath)
        data.forEach { line ->
            val parts = line.split(",")
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

    fun saveToFile() {
        val data = organos.joinToString("\n") { organo ->
            "${organo.id},${organo.nombre},${organo.funcion},${organo.cantidadCelulas},${organo.eficiencia}"
        }
        FileManager.write(filePath, data)
    }
}
