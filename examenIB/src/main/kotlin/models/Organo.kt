package models

data class Organo(
    val id: Int,
    var nombre: String,
    var funcion: String,
    var cantidadCelulas: Int,
    var eficiencia: Double
)
