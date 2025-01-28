package com.example.ccgr12024b_wjia

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "serVivoDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla SerVivo
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

        // Crear tabla Organo
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

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Organo")
        db?.execSQL("DROP TABLE IF EXISTS SerVivo")
        onCreate(db)
    }

    // Métodos para SerVivo
    fun agregarSerVivo(nombre: String, tipo: String, esVertebrado: Boolean, fechaNacimiento: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("esVertebrado", if (esVertebrado) 1 else 0)
            put("fechaNacimiento", fechaNacimiento)
        }
        val resultado = db.insert("SerVivo", null, values)
        db.close()
        return resultado
    }

    fun obtenerTodosLosSeresVivos(): List<SerVivo> {
        val lista = mutableListOf<SerVivo>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SerVivo", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                val esVertebrado = cursor.getInt(cursor.getColumnIndexOrThrow("esVertebrado")) == 1
                val fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento"))

                lista.add(SerVivo(id, nombre, tipo, esVertebrado, fechaNacimiento))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    // Métodos para SerVivo
    fun agregarOrgano(nombre: String,funcion:String,cantidadCelulas:Int,eficiencia:Double, serVivoId:Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("funcion", funcion)
            put("cantidadCelulas", cantidadCelulas)
            put("eficiencia", eficiencia)
            put("serVivo_id", serVivoId)
        }
        val resultado = db.insert("Organo", null, values)
        db.close()
        return resultado
    }

}