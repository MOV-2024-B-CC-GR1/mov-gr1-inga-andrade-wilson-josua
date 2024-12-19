import models.Organo
import services.OrganoService
import services.SerVivoService

fun main() {
    val serVivoService = SerVivoService()
    val organoService = OrganoService()  // Uso del servicio OrganoService
    val fileName = "seresVivos.txt"

    // Cargar datos desde archivo al inicio
    serVivoService.loadFromFile(fileName)
    println("Datos cargados desde el archivo $fileName")

    while (true) {
        println("\n*** Menú CRUD ***")
        println("1. Crear Ser Vivo")
        println("2. Listar Seres Vivos")
        println("3. Actualizar Ser Vivo")
        println("4. Eliminar Ser Vivo")
        println("5. Crear Órgano")
        println("6. Leer Órganos")
        println("7. Actualizar Órgano")
        println("8. Eliminar Órgano")
        println("9. Salir")

        when (readln().toInt()) {
            // ... (Opciones de CRUD para Seres Vivos)

            5 -> {
                println("ID del Ser Vivo al que pertenece el Órgano:")
                val serVivoId = readln().toInt()
                val serVivo = serVivoService.read().find { it.id == serVivoId }

                if (serVivo != null) {
                    println("Nombre del Órgano:")
                    val nombre = readln()
                    println("Función del Órgano:")
                    val funcion = readln()
                    println("Es vital (true/false):")
                    val esVital = readln().toBoolean()
                    println("Peso en gramos:")
                    val peso = readln().toDouble()
                    println("Número de células:")
                    val numCelulas = readln().toInt()

                    val organo = Organo(
                        id = (organoService.read().maxOfOrNull { it.id } ?: 0) + 1,
                        nombre = nombre,
                        funcion = funcion,
                        cantidadCelulas = cantidadCelulas,
                        peso = peso,
                        numCelulas = numCelulas
                    )
                    organoService.create(organo) // Crear órgano y guardarlo
                    serVivo.organos.add(organo)
                } else {
                    println("No se encontró un Ser Vivo con ID $serVivoId")
                }
            }

            6 -> {
                println("Órganos registrados:")
                organoService.read().forEach { println(it) } // Leer y mostrar órganos
            }

            7 -> {
                println("ID del Órgano a actualizar:")
                val id = readln().toInt()
                val organo = organoService.read().find { it.id == id }

                if (organo != null) {
                    println("Nuevo Nombre (actual: ${organo.nombre}):")
                    val nombre = readln()
                    println("Nueva Función (actual: ${organo.funcion}):")
                    val funcion = readln()

                } else {
                    println("No se encontró un Órgano con ID $id")
                }
            }

            8 -> {
                println("ID del Órgano a eliminar:")
                val id = readln().toInt()
                organoService.delete(id) // Eliminar órgano
            }

            9 -> {
                serVivoService.saveToFile(fileName)  // Guardar seres vivos
                organoService.saveToFile()          // Guardar órganos
                println("Datos guardados. ¡Hasta luego!")
                return
            }

            else -> println("Opción inválida.")
        }
    }
}
