package models

data class SerVivo(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val esVertebrado: Boolean,
    val edadSerVivo: Int,
    val organos: List<Organo> = emptyList()
)

