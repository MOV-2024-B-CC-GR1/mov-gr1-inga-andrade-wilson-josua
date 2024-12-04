import models.SerVivo
import models.Organo
import services.SerVivoService
import services.OrganoService

fun main() {
    val serVivoService = SerVivoService()
    val organoService = OrganoService()

    while (true) {
        println("\n")
        println(">=======================Menú=========================<")
        println("1. Crear Ser Vivo")
        println("2. Listar Seres Vivos")
        println("3. Actualizar Ser Vivo")
        println("4. Eliminar Ser Vivo")
        println("5. Crear Órgano")
        println("6. Listar Órganos")
        println("7. Actualizar Órgano")
        println("8. Eliminar Órgano")
        println("9. Salir")
        println(">===================================================<")
        println("Por favor, presiona un número para elegir una opción:")

        try {
            when (readln().toInt()) {
                1 -> crearSerVivo(serVivoService, organoService)
                2 -> listarSeresVivos(serVivoService)
                3 -> actualizarSerVivo(serVivoService, organoService)
                4 -> eliminarSerVivo(serVivoService)
                5 -> crearOrgano(organoService)
                6 -> listarOrganos(organoService)
                7 -> actualizarOrgano(organoService)
                8 -> eliminarOrgano(organoService)
                9 -> {
                    println("Fin de Ejecución.")
                    return
                }
                else -> println("Opción inválida. Por favor, selecciona una opción del 1 al 9.")
            }
        } catch (e: NumberFormatException) {
            println("Entrada inválida. Por favor, ingresa un número válido.")
        } catch (e: IllegalArgumentException) {
            println("Error: ${e.message}")
        } catch (e: Exception) {
            println("Ocurrió un error inesperado: ${e.message}")
        }
    }
}

fun crearSerVivo(serVivoService: SerVivoService, organoService: OrganoService) {
    println("Nombre del Ser Vivo:")
    val nombre = leerValorNoVacio()
    println("Tipo del Ser Vivo:")
    val tipo = leerValorNoVacio()
    println("Es vertebrado (true/false):")
    val esVertebrado = readln().toBoolean()
    println("Edad del Ser Vivo:")
    val edadSerVivo = leerValorInt()

    println("¿Quieres agregar órganos a este Ser Vivo? (sí/no):")
    val agregarOrganos = readln().lowercase() == "sí"

    val organos = if (agregarOrganos) {
        agregarOrganosAserVivo(organoService)
    } else {
        emptyList()
    }

    val serVivo = SerVivo(
        id = (serVivoService.read().maxOfOrNull { it.id } ?: 0) + 1,
        nombre = nombre,
        tipo = tipo,
        esVertebrado = esVertebrado,
        edadSerVivo = edadSerVivo,
        organos = organos
    )
    serVivoService.create(serVivo)
}

fun leerValorNoVacio(): String {
    var valor: String
    while (true) {
        valor = readln().trim()
        if (valor.isNotEmpty()) break
        println("El valor no puede estar vacío. Por favor, ingresa un valor válido:")
    }
    return valor
}

fun leerValorInt(): Int {
    while (true) {
        val input = readln().trim()
        if (input.isNotEmpty() && input.toIntOrNull() != null) {
            return input.toInt()
        } else {
            println("Por favor, ingresa un número entero válido:")
        }
    }
}

fun agregarOrganosAserVivo(organoService: OrganoService): List<Organo> {
    val listaOrganos = mutableListOf<Organo>()
    while (true) {
        println("Lista de órganos disponibles:")
        organoService.read().forEach { println(it) }
        println("ID del órgano a agregar (o presiona 0 para finalizar):")
        val idOrgano = readln().toInt()
        if (idOrgano == 0) break
        val organo = organoService.read().find { it.id == idOrgano }
        if (organo != null) {
            listaOrganos.add(organo)
        } else {
            println("Órgano no encontrado.")
        }
    }
    return listaOrganos
}

fun listarSeresVivos(serVivoService: SerVivoService) {
    println("Seres Vivos registrados:")
    serVivoService.read().forEach { println(it) }
}

fun actualizarSerVivo(serVivoService: SerVivoService, organoService: OrganoService) {
    println("Seres Vivos registrados:")
    serVivoService.read().forEach { println(it) }
    println("\nID del Ser Vivo a actualizar:")
    val id = leerValorInt()
    val serVivo = serVivoService.read().find { it.id == id }

    if (serVivo != null) {
        println("Nuevo Nombre (actual: ${serVivo.nombre}):")
        val nombre = leerValorNoVacio()
        println("Nuevo Tipo (actual: ${serVivo.tipo}):")
        val tipo = leerValorNoVacio()
        println("Es vertebrado (true/false) (actual: ${serVivo.esVertebrado}):")
        val esVertebrado = readln().toBoolean()
        println("Nueva Edad (actual: ${serVivo.edadSerVivo}):")
        val edadServivo = leerValorInt()

        println("¿Quieres actualizar los órganos asociados? (si/no):")
        val actualizarOrganos = readln().lowercase() == "si"

        val organos = if (actualizarOrganos) {
            actualizarOrganosSerVivo(serVivo, organoService)
        } else {
            serVivo.organos
        }

        val updatedSerVivo = SerVivo(id, nombre, tipo, esVertebrado, edadServivo, organos)
        serVivoService.update(id, updatedSerVivo)
        println("Ser Vivo actualizado con éxito.")
    } else {
        println("No se encontró un Ser Vivo con ID $id")
    }
}

fun actualizarOrganosSerVivo(serVivo: SerVivo, organoService: OrganoService): List<Organo> {
    val listaOrganos = mutableListOf<Organo>()
    // Agregar órganos nuevos
    while (true) {
        println("Lista de órganos disponibles:")
        organoService.read().forEach { println(it) }
        println("ID del órgano a agregar (o presiona 0 para finalizar):")
        val idOrgano = readln().toInt()
        if (idOrgano == 0) break
        val organo = organoService.read().find { it.id == idOrgano }
        if (organo != null) {
            listaOrganos.add(organo)
        } else {
            println("Órgano no encontrado.")
        }
    }
    // Quitar órganos existentes
    println("¿Quieres quitar órganos del Ser Vivo? (si/no):")
    val quitarOrganos = readln().lowercase() == "si"
    if (quitarOrganos) {
        println("Órganos actuales del Ser Vivo:")
        serVivo.organos.forEach { println(it) }
        while (true) {
            println("ID del órgano a quitar (o presiona 0 para finalizar):")
            val idOrgano = readln().toInt()
            if (idOrgano == 0) break
            val organoAEliminar = serVivo.organos.find { it.id == idOrgano }
            if (organoAEliminar != null) {
                listaOrganos.remove(organoAEliminar)
                println("Órgano eliminado.")
            } else {
                println("Órgano no encontrado.")
            }
        }
    }
    return listaOrganos
}

fun eliminarSerVivo(serVivoService: SerVivoService) {
    println("Seres Vivos registrados:")
    serVivoService.read().forEach { println(it) }
    println("\nID del Ser Vivo a eliminar:")
    val id = leerValorInt()
    serVivoService.delete(id)
    println("Ser Vivo eliminado con éxito.")
}

fun crearOrgano(organoService: OrganoService) {
    println("Nombre del Órgano:")
    val nombre = leerValorNoVacio()
    println("Función del Órgano:")
    val funcion = leerValorNoVacio()
    println("Número de células:")
    val cantidadCelulas = leerValorInt()
    println("Eficiencia del órgano:")
    val eficiencia = leerValorDouble()

    val organo = Organo(
        id = (organoService.read().maxOfOrNull { it.id } ?: 0) + 1,
        nombre = nombre,
        funcion = funcion,
        cantidadCelulas = cantidadCelulas,
        eficiencia = eficiencia
    )
    organoService.create(organo)
    println("Órgano creado con éxito.")
}

fun leerValorDouble(): Double {
    while (true) {
        val input = readln().trim()
        if (input.isNotEmpty() && input.toDoubleOrNull() != null) {
            return input.toDouble()
        } else {
            println("Por favor, ingresa un número decimal válido:")
        }
    }
}

fun listarOrganos(organoService: OrganoService) {
    println("Órganos registrados:")
    organoService.read().forEach { println(it) }
}

fun actualizarOrgano(organoService: OrganoService) {
    println("Órganos registrados:")
    organoService.read().forEach { println(it) }
    println("\nID del Órgano a actualizar:")
    val id = leerValorInt()
    val organo = organoService.read().find { it.id == id }

    if (organo != null) {
        println("Nuevo Nombre (actual: ${organo.nombre}):")
        val nombre = leerValorNoVacio()
        println("Nueva Función (actual: ${organo.funcion}):")
        val funcion = leerValorNoVacio()
        println("Nueva cantidad de células (actual: ${organo.cantidadCelulas}):")
        val cantidadCelulas = leerValorInt()
        println("Nueva eficiencia (actual: ${organo.eficiencia}):")
        val eficiencia = leerValorDouble()

        val updatedOrgano = Organo(id, nombre, funcion, cantidadCelulas, eficiencia)
        organoService.update(id, updatedOrgano)
        println("Órgano actualizado con éxito.")
    } else {
        println("No se encontró un Órgano con ID $id")
    }
}

fun eliminarOrgano(organoService: OrganoService) {
    println("Órganos registrados:")
    organoService.read().forEach { println(it) }
    println("\nID del Órgano a eliminar:")
    val id = leerValorInt()
    organoService.delete(id)
    println("Órgano eliminado con éxito.")
}
