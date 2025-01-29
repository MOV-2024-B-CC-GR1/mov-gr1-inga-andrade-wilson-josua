package com.example.ccgr12024b_wjia

data class Organo(
    val id: Int,              // Identificador único del órgano.
    val nombre: String,       // Nombre del órgano.
    val funcion: String,      // Función principal del órgano.
    val cantidadCelulas: Int, // Número aproximado de células en el órgano.
    val eficiencia: Double,   // Eficiencia funcional del órgano en porcentaje
    val serVivoId: Int
)