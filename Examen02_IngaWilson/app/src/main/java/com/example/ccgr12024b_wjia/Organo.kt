package com.example.ccgr12024b_wjia

data class Organo(
    val id: Int,              // Identificador único del órgano.
    var nombre: String,       // Nombre del órgano.
    var funcion: String,      // Función principal del órgano.
    var cantidadCelulas: Int, // Número aproximado de células en el órgano.
    var eficiencia: Double    // Eficiencia funcional del órgano en porcentaje.
)