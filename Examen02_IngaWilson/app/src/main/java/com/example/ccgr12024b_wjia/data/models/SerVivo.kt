package com.example.ccgr12024b_wjia

data class SerVivo(
    val id: Int,                   // Identificador único del ser vivo.
    val nombre: String,            // Nombre del ser vivo.
    val tipo: String,              // Tipo de ser vivo (mamífero, pez, reptil, anfibio, ave, etc...).
    val esVertebrado: Boolean,     // Indica si el ser vivo es vertebrado (true/false).
    val fechaNacimiento: String    // Fecha de nacimiento del ser vivo.

)
