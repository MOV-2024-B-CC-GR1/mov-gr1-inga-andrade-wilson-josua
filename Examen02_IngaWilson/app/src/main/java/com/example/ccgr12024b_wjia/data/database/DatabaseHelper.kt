package com.example.ccgr12024b_wjia

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase que maneja la creación, actualización y acceso a la base de datos
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "serVivoDB", null, 1) {

    // Se ejecuta cuando la base de datos es creada por primera vez
    override fun onCreate(db: SQLiteDatabase?) {
        // Crear la tabla SerVivo con columnas id, nombre, tipo, esVertebrado, y fechaNacimiento
        db?.execSQL(
            """
                CREATE TABLE SerVivo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    tipo TEXT NOT NULL,
                    esVertebrado INTEGER NOT NULL,
                    fechaNacimiento TEXT NOT NULL
                )
            """
        )

        // Crear la tabla Organo con columnas id, nombre, funcion, cantidadCelulas, eficiencia, y serVivo_id
        // serVivo_id tiene una clave foránea que hace referencia a la tabla SerVivo
        db?.execSQL(
            """
                CREATE TABLE Organo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    funcion TEXT NOT NULL,
                    cantidadCelulas INTEGER NOT NULL,
                    eficiencia REAL NOT NULL,
                    serVivo_id INTEGER NOT NULL,
                    FOREIGN KEY (serVivo_id) REFERENCES SerVivo(id) ON DELETE CASCADE
                )
            """
        )
    }

    // Se ejecuta cuando la base de datos necesita ser actualizada (cambio de versión)
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Eliminar tablas existentes
        db?.execSQL("DROP TABLE IF EXISTS Organo")
        db?.execSQL("DROP TABLE IF EXISTS SerVivo")

        // Llamar a onCreate para crear las tablas nuevamente
        onCreate(db)
    }

    // Métodos para trabajar con la tabla SerVivo

    // Agregar un nuevo SerVivo a la base de datos
    fun agregarSerVivo(nombre: String, tipo: String, esVertebrado: Boolean, fechaNacimiento: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("esVertebrado", if (esVertebrado) 1 else 0)  // Convertir Booleano a entero
            put("fechaNacimiento", fechaNacimiento)
        }
        val resultado = db.insert("SerVivo", null, values)  // Insertar el nuevo SerVivo
        db.close()  // Cerrar la base de datos
        return resultado  // Retornar el ID de la fila insertada
    }

    // Obtener todos los SeresVivos desde la base de datos
    fun obtenerTodosLosSeresVivos(): List<SerVivo> {
        val lista = mutableListOf<SerVivo>()  // Lista para almacenar los SeresVivos
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SerVivo", null)  // Consultar todos los SeresVivos

        // Procesar los resultados de la consulta
        if (cursor.moveToFirst()) {
            do {
                // Obtener los valores de cada columna
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                val esVertebrado = cursor.getInt(cursor.getColumnIndexOrThrow("esVertebrado")) == 1
                val fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento"))

                // Crear el objeto SerVivo y agregarlo a la lista
                lista.add(SerVivo(id, nombre, tipo, esVertebrado, fechaNacimiento))
            } while (cursor.moveToNext())  // Continuar si hay más filas
        }

        cursor.close()  // Cerrar el cursor
        db.close()  // Cerrar la base de datos
        return lista  // Retornar la lista de SeresVivos
    }

    // Métodos para trabajar con la tabla Organo

    // Agregar un nuevo Organo a la base de datos
    fun agregarOrgano(nombre: String, funcion: String, cantidadCelulas: Int, eficiencia: Double, serVivoId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("funcion", funcion)
            put("cantidadCelulas", cantidadCelulas)
            put("eficiencia", eficiencia)
            put("serVivo_id", serVivoId)  // Asociar el órgano a un SerVivo
        }
        val resultado = db.insert("Organo", null, values)  // Insertar el nuevo Organo
        db.close()  // Cerrar la base de datos
        return resultado  // Retornar el ID de la fila insertada
    }
}
