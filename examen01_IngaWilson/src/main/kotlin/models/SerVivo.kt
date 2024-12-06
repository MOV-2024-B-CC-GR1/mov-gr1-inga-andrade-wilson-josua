package models

import java.time.LocalDate

data class SerVivo(
    val id: Int,                   // Identificador único del ser vivo.
    val nombre: String,            // Nombre del ser vivo.
    val tipo: String,              // Tipo de ser vivo (mamífero, pez, reptil, anfibio, ave, etc...).
    val esVertebrado: Boolean,     // Indica si el ser vivo es vertebrado (true/false).
    val fechaNacimiento: LocalDate,          // Fecha de nacimiento del ser vivo.
    val organos: List<Organo> = emptyList() // Lista de órganos asociados al ser vivo, inicializada vacía.
)

