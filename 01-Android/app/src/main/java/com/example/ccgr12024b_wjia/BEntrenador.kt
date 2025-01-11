package com.example.ccgr12024b_wjia

class BEntrenador(
    var id: Int,
    var nombre: String,
    var descripcion: String?
) {
    override fun toString(): String {
        return "$nombre ${descripcion}"
    }
}