package models

data class SerVivo(
    val id: Int,
    var nombre: String,
    var tipo: String,
    var esVertebrado: Boolean,
    var promedioEsperanzaVida: Double,
    val organos: MutableList<Organo> = mutableListOf()
)

