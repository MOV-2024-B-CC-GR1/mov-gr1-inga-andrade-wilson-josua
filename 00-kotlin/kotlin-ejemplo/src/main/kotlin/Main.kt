import java.util.*
import kotlin.collections.ArrayList

fun main(args: Array<String>) {

    calcularSueldo(10.00) //solo parametro requerido
    calcularSueldo(10.00,15.00,20.00) // parametro requerido y
                                                                 // solo escribir pareametros opcionales

    // Named parameters
    // calcularSueldo(sueldo, tasa, bonoEspecial)
    calcularSueldo(10.00, bonoEspecial = 20.00)  //usando el parametro bonoEscpecial en 2da posicion

    calcularSueldo(bonoEspecial = 20.00, sueldo = 10.00, tasa = 14.00)
    // usando el parametro bonoEspecial en 1ra posicion
    // usando el parametro sueldo en 2da posicion
    // usando el parametro tasa en 3ra posicion
    // gracias a los parametros nombrados

    //CLASES USO:
    val sumaA = Suma(1,1)
    val sumaB = Suma(null, 1)
    val sumaC = Suma(1, null)
    val sumaD = Suma(null, null)
    sumaA.sumar()
    sumaB.sumar()
    sumaC.sumar()
    sumaD.sumar()
    println(Suma.pi)
    println(Suma.elevarAlCuadrado(2))
    println(Suma.historialSumas)


    // Arreglos
    // Estáticos
    val arregloEstatico: Array<Int> = arrayOf<Int>(1,2,3)
    println(arregloEstatico);

    //Dinamicos
    val arregloDinamico: ArrayList<Int> = arrayListOf<Int>(1,2,3,4,5,6,7,8,9,10)

    println(arregloDinamico)
    arregloDinamico.add(1)
    arregloDinamico.add(12)
    println(arregloDinamico)

    //FOR EACH = > Unit
    // Iterar un arreglo
    val respuestaForEach: Unit = arregloDinamico
        .forEach { valorActual: Int -> // - > = >
            println("Valor Actual: ${valorActual}");
        }
    // "it" (en ingles "eso") significa el elemento iterado
    arregloDinamico.forEach { println("Valor Actual (it): ${it}") }

    // MAP -> mUTA (Modifica cambia) el arreglo
    // 1) Enviamos el nuevo valor de la iteracion
    // 2) Nos devuelve un NUEVO ARREGLO con valores
    // de las iteraciones
    val respuestaMap: List<Double> = arregloDinamico
        .map { valorActual: Int ->
            return@map valorActual.toDouble()+100.00
        }
    // Filter - > Filtrar el ARREGLO
    // 1) Devolver una expresion (True o Falso)
    // 2) Nuevo arreglo FILTRADO
     val respuestaFilter: List<Int> = arregloDinamico
         .filter{valorActual:Int ->
             // Expresion a CONDICION
             val mayoresACinco: Boolean = valorActual > 5
             return@filter mayoresACinco
         }
     val respuestaFilterDos = arregloDinamico.filter { it <= 5 }
        println(respuestaFilter)
        println(respuestaFilterDos)
     // OR AND
     // OR -> ANY (Alguno cumple?)
     // AND -> ALL (Todos cumplen?)
     val respuestaAny: Boolean = arregloDinamico
         .any { valorActual: Int ->
             return@any (valorActual > 5)
         }
    println(respuestaAny) // True
    val respuestaAll: Boolean = arregloDinamico
        .all { valorActual: Int ->
            return@all (valorActual > 5 )
        }
    println(respuestaAll) // False

    //REDUCE ->valor acumulado
    //valor acumulado = 0 (Siempre empiezo en 0 en Kotlin)
    // [1,2,3,4,5] -> Acumular "SUMAR" estos valores del arreglo
    // valorIteracion1 = valorEmpieza + 1 = 0 + 1 = 1 -> Iteracion1
    // valorIteracion2 = valorIteracion1 + 2 = 1 + 2 = 3 -> Iteracion2
    // valorIteracion3 = valorIteracion2 + 3 = 3 + 3 = 6 -> Iteracion3
    // valorIteracion4 = valorIteracion3 + 4 = 6 + 4 = 10 -> Iteracion4
    // valorIteracion5 = valorIteracion4 + 5 = 10 + 5 = 15 -> Iteracion5
    val respuestaReduce: Int = arregloDinamico
        .reduce{acumulado:Int, valorActual:Int ->
            return@reduce(acumulado + valorActual) // -> Cambiar o usar la logica de negocio
        }
    println(respuestaReduce)
    // return@reduce acumulado + (itemCarrito,cantidad * itemCarrito.precio)




}

fun calcularSueldo(
    sueldo:Double, // Requerido
    tasa:Double = 12.00, // Opcional (defecto)
    bonoEspecial: Double? = null // Opcional (nullable)
    //Variable? -> "?" Es Nullable (osea que puede em algun momento ser nulo)
): Double {
    // Int -> int? (nullable)
    // String -> String (nullable)
    // Date -> Date? (nullable)
    if(bonoEspecial == null){
        return sueldo * (100/tasa)
    }else{
        return sueldo * (100/tasa) * bonoEspecial
    }
}

abstract class NumerosJava{
    protected val numeroUno: Int
    private val numeroDos: Int

    constructor(
        uno:Int,
        dos:Int
    ){
        this.numeroUno = uno
        this.numeroDos = dos
        println("Inicializando")
    }
}

abstract class Numeros( //Constructor Primario
    // Caso 1) Parametro normal
    //uno:Init , (parametro (sin modificador de acceso))

    // Caso 2) Parametro y propiedad (atributo) (protected)
    // private var uno:Init (propiedada "instancia.uno)
    protected val numeroUno: Int, // instancia.numeroUno
    protected val numeroDos: Int, // instancia.numeroDos
    parametroNoUsadoNoPropiedadDeLaClase:Int? = null

) {
    init{ //bloque constructor primario OPCIONAL
        this.numeroUno
        this.numeroDos
        println("Inicilizando")
    }
}

class Suma( // Constructor primario
    unoParametro: Int,  // Parametro
    dosParametro: Int,  // Parametro
): Numeros( // Clase papa, Numeros (extendiendo)
    unoParametro,
    dosParametro
){
    public val soyPublicoExplicito: String = "Publicas"
    val soyPublicoImplicite: String = "Publico implicito"

    init{ // bloque constructor primario
        this.numeroUno
        this.numeroDos
        numeroUno // this. OPCIONAL [propiedades, métodos]
        numeroDos // this. OPCIONAL [propiedades, métodos]
        this.soyPublicoImplicite
        soyPublicoExplicito
    }
    constructor( // Constructor secundario
        uno: Int?, // Entero nullable
        dos: Int,

    ):this(
        if(uno == null)  0 else uno,
        dos
    ){
        // Bloque de codigo de constructor secundario
    }
    constructor( // Constructor secundario
        uno: Int,
        dos: Int?, // Entero nullable
    ):this(
        uno,
        if(dos == null)  0 else dos,
    )
    constructor( // Constructor secundario
        uno: Int?, // Entero nullable
        dos: Int?, // Entero nullable

    ):this(
        if(uno == null)  0 else uno,
        if(dos == null)  0 else dos
    )
    fun sumar():Int{
        val total = numeroUno + numeroDos
        agregarHistorial(total)
        return total
    }

    companion object{ // Comparte entre todas las instancias, similar al STATIC
        // funciones, variables
        // NombreClase.metodo, NombreClase.funcion ==>
        // Suma.pi
        val pi = 3.14
        // Suma.elevarAlCuadrado
        fun elevarAlCuadrado(num:Int):Int{return num * num }
        val historialSumas = arrayListOf<Int>()
    }
    fun agregarHistorial(valorTotalSuma:Int){ // Suma.agregarHistorial
        historialSumas.add(valorTotalSuma)
    }






}




