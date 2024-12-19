import models.SerVivo
import models.Organo
import services.SerVivoService
import services.OrganoService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun main() {
    // Inicialización de servicios para gestionar Seres Vivos y Órganos
    val serVivoService = SerVivoService()
    val organoService = OrganoService()

    // Ciclo principal del menú
    while (true) {
        mostrarMenu()
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
                    return // Salir del programa
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

// Mostrar el menú principal
fun mostrarMenu() {
    println("\n")
    println("|=================== MENU PRINCIPAL ==================|")
    println("|------- SER VIVO ---------||--------- ORGANO --------|")
    println("|1. Crear Ser Vivo         || 5. Crear Organo         |")
    println("|2. Listar Seres Vivos     || 6. Listar Organos       |")
    println("|3. Actualizar Ser Vivo    || 7. Actualizar Organo    |")
    println("|4. Eliminar Ser Vivo      || 8. Eliminar Organo      |")
    println("|--------------------------||-------------------------|")
    println("|9. Salir                                             |")
    println("|=====================================================|")
    println("\nPor favor, ingresa el numero correspondiente a la opcion deseada:")
}


//Funciones para interactuar con Seres Vivos

// Función para crear un nuevo Ser Vivo
fun crearSerVivo(serVivoService: SerVivoService, organoService: OrganoService) {
    println("Nombre del Ser Vivo:")
    val nombre = leerValorNoVacio()
    println("Tipo del Ser Vivo:")
    val tipo = leerValorNoVacio()
    println("Es vertebrado (true/false):")
    val esVertebrado = readln().toBoolean()
    println("Fecha de nacimiento (yyyy-MM-dd):")
    val fechaNacimiento = leerFechaValida()

    // Pregunta si el usuario quiere asociar órganos al Ser Vivo
    println("¿Quieres agregar órganos a este Ser Vivo? (s/n):")
    val agregarOrganos = leerConfirmacion()

    val organos = if (agregarOrganos) {
        agregarOrganosAserVivo(organoService) // Permite seleccionar órganos existentes
    } else {
        emptyList() // Lista vacía si no se agregan órganos
    }

    // Crea y guarda el nuevo Ser Vivo
    val serVivo = SerVivo(
        id = (serVivoService.read().maxOfOrNull { it.id } ?: 0) + 1,
        nombre = nombre,
        tipo = tipo,
        esVertebrado = esVertebrado,
        fechaNacimiento = fechaNacimiento,
        organos = organos
    )
    serVivoService.create(serVivo)
}

// Función para listar todos los Seres Vivos registrados
fun listarSeresVivos(serVivoService: SerVivoService) {
    println("Seres Vivos registrados:")
    serVivoService.read().forEach { println(it) }
}

// Función para actualizar un Ser Vivo existente
fun actualizarSerVivo(serVivoService: SerVivoService, organoService: OrganoService) {
    // Muestra todos los Seres Vivos registrados
    println("Seres Vivos registrados:")
    serVivoService.read().forEach { println(it) }
    println("\nID del Ser Vivo a actualizar:")
    val id = leerValorInt() // Lee el ID del Ser Vivo a actualizar
    val serVivo = serVivoService.read().find { it.id == id }

    if (serVivo != null) {
        // Solicita nuevos valores para actualizar el Ser Vivo
        println("Nuevo Nombre (actual: ${serVivo.nombre}):")
        val nombre = leerValorNoVacio()
        println("Nuevo Tipo (actual: ${serVivo.tipo}):")
        val tipo = leerValorNoVacio()
        println("Es vertebrado (true/false) (actual: ${serVivo.esVertebrado}):")
        val esVertebrado = readln().toBoolean()
        println("Nueva fecha de nacimiento (actual: ${serVivo.fechaNacimiento}):")
        val fechaNacimiento = leerFechaValida()

        // Pregunta si se desean actualizar los órganos asociados
        println("¿Quieres actualizar los órganos asociados? (s/n):")
        val actualizarOrganos = leerConfirmacion()

        val organos = if (actualizarOrganos) {
            // Permite actualizar los órganos asociados al Ser Vivo
            actualizarOrganosSerVivo(serVivo, organoService)
        } else {
            serVivo.organos // Si no se actualizan, mantiene los órganos actuales
        }

        // Crea el Ser Vivo actualizado y lo guarda
        val updatedSerVivo = SerVivo(id, nombre, tipo, esVertebrado, fechaNacimiento, organos)
        serVivoService.update(id, updatedSerVivo)
        println("Ser Vivo actualizado con éxito.")
    } else {
        println("No se encontró un Ser Vivo con ID $id")
    }
}

// Función para eliminar un Ser Vivo
fun eliminarSerVivo(serVivoService: SerVivoService) {
    // Muestra todos los Seres Vivos registrados
    println("Seres Vivos registrados:")
    serVivoService.read().forEach { println(it) }
    println("\nID del Ser Vivo a eliminar:")
    val id = leerValorInt() // Lee el ID del Ser Vivo a eliminar
    serVivoService.delete(id) // Elimina el Ser Vivo
    println("Ser Vivo eliminado con éxito.")
}

//Funciones para interactuar con Organos

// Función para crear un nuevo Organo
fun crearOrgano(organoService: OrganoService) {
    println("Nombre del Organo:")
    val nombre = leerValorNoVacio() // Lee el nombre del órgano
    println("Función del Organo:")
    val funcion = leerValorNoVacio() // Lee la función del órgano
    println("Número de células:")
    val cantidadCelulas = leerValorInt() // Lee la cantidad de células del órgano
    println("Eficiencia del organo:")
    val eficiencia = leerValorDouble() // Lee la eficiencia del órgano

    // Crea un nuevo Organo
    val organo = Organo(
        id = (organoService.read().maxOfOrNull { it.id } ?: 0) + 1,
        nombre = nombre,
        funcion = funcion,
        cantidadCelulas = cantidadCelulas,
        eficiencia = eficiencia
    )
    organoService.create(organo) // Guarda el nuevo Organo
    println("Organo creado con éxito.")
}

// Función para listar todos los Organos registrados
fun listarOrganos(organoService: OrganoService) {
    println("Organos registrados:")
    organoService.read().forEach { println(it) }
}

// Función para actualizar un Organo existente
fun actualizarOrgano(organoService: OrganoService) {
    println("Organos registrados:")
    organoService.read().forEach { println(it) }
    println("\nID del Organo a actualizar:")
    val id = leerValorInt() // Lee el ID del Organo a actualizar
    val organo = organoService.read().find { it.id == id }

    if (organo != null) {
        // Solicita nuevos valores para actualizar el Órgano
        println("Nuevo Nombre (actual: ${organo.nombre}):")
        val nombre = leerValorNoVacio()
        println("Nueva Función (actual: ${organo.funcion}):")
        val funcion = leerValorNoVacio()
        println("Nueva cantidad de células (actual: ${organo.cantidadCelulas}):")
        val cantidadCelulas = leerValorInt()
        println("Nueva eficiencia (actual: ${organo.eficiencia}):")
        val eficiencia = leerValorDouble()

        // Crea el Organo actualizado y lo guarda
        val updatedOrgano = Organo(id, nombre, funcion, cantidadCelulas, eficiencia)
        organoService.update(id, updatedOrgano)
        println("Organo actualizado con éxito.")
    } else {
        println("No se encontró un Organo con ID $id")
    }
}

// Función para eliminar un Organo
fun eliminarOrgano(organoService: OrganoService) {
    println("Organos registrados:")
    organoService.read().forEach { println(it) }
    println("\nID del Organo a eliminar:")
    val id = leerValorInt() // Lee el ID del Órgano a eliminar
    organoService.delete(id) // Elimina el Órgano
    println("Organo eliminado con éxito.")
}

// Funciones de Relacion SerVivo-Organo

// Función para agregar organos  a un Ser Vivo
fun agregarOrganosAserVivo(organoService: OrganoService): List<Organo> {
    val listaOrganos = mutableListOf<Organo>()
    while (true) {
        println("Lista de organos disponibles:")
        organoService.read().forEach { println(it) }
        println("ID del órgano a agregar (o presiona 0 para finalizar):")
        val idOrgano = readln().toInt()
        if (idOrgano == 0) break
        val organo = organoService.read().find { it.id == idOrgano }
        if (organo != null) {
            listaOrganos.add(organo)
        } else {
            println("Organo no encontrado.")
        }
    }
    return listaOrganos
}

// Función para actualizar los organos asociados a un Ser Vivo
fun actualizarOrganosSerVivo(serVivo: SerVivo, organoService: OrganoService): List<Organo> {
    val listaOrganos = mutableListOf<Organo>()
    // Agregar organos nuevos
    while (true) {
        println("Lista de organos disponibles:")
        organoService.read().forEach { println(it) }
        println("ID del organo a agregar (o presiona 0 para finalizar):")
        val idOrgano = readln().toInt()
        if (idOrgano == 0) break
        val organo = organoService.read().find { it.id == idOrgano }
        if (organo != null) {
            listaOrganos.add(organo)
        } else {
            println("Organo no encontrado.")
        }
    }

    // Opción para quitar organos existentes
    println("¿Quieres quitar órganos del Ser Vivo? (s/n):")
    val quitarOrganos = leerConfirmacion()
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
                println("Organo eliminado.")
            } else {
                println("Organo no encontrado.")
            }
        }
    }
    return listaOrganos
}

//Funciones auxiliares para la validación

// Pregunta si se desean actualizar los órganos asociados
fun leerConfirmacion(): Boolean {
    while (true) {
        val respuesta = readln().lowercase()
        if (respuesta == "s") {
            return true
        } else if (respuesta == "n") {
            return false
        } else {
            println("Entrada inválida. Por favor, ingresa 's' para sí o 'n' para no.")
        }
    }
}
// Función para leer una cadena no vacía
fun leerValorNoVacio(): String {
    var valor: String
    while (true) {
        valor = readln().trim()
        if (valor.isNotEmpty()) break
        println("El valor no puede estar vacío. Por favor, ingresa un valor válido:")
    }
    return valor
}

//Función para leer una fecha válida
fun leerFechaValida(): LocalDate {
    val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    while (true) {
        val entrada = readln()
        try {
            return LocalDate.parse(entrada, formato).also {
                println("Fecha válida ingresada: $it")
            }
        } catch (e: DateTimeParseException) {
            println("La fecha ingresada no es válida. Intenta nuevamente.")
        }
    }
}

// Función para leer un número entero
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

// Función para leer un número decimal
fun leerValorDouble(): Double {
    while (true) {
        val input = readln().trim()
        if (input.isNotEmpty() && input.toDoubleOrNull() != null) {
            return input.toDouble() // Devuelve el valor decimal
        } else {
            println("Por favor, ingresa un número decimal válido:")
        }
    }
}







