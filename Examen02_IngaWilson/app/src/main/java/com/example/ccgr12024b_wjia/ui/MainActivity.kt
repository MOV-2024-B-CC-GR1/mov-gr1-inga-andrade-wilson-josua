package com.example.ccgr12024b_wjia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Inicialización de variables
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaSeresVivos: ListView
    private lateinit var seresVivos: MutableList<SerVivo>
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Activar pantalla completa para aprovechar el espacio de la pantalla
        setContentView(R.layout.activity_main)

        // Ajuste de márgenes para la barra de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de los objetos
        dbHelper = DatabaseHelper(this)
        listaSeresVivos = findViewById(R.id.listaSeresVivos)

        // Obtener los seres vivos desde la base de datos
        seresVivos = obtenerSeresVivos()

        // Crear un adaptador con los nombres de los seres vivos
        val nombres = seresVivos.map { it.nombre }.toMutableList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listaSeresVivos.adapter = adapter

        // Configuración del botón para crear un nuevo ser vivo
        findViewById<Button>(R.id.btnCrearSerVivo).setOnClickListener {
            val intent = Intent(this, SerVivoEditorActivity::class.java)
            startActivity(intent)
        }

        // Configuración del clic largo sobre un elemento de la lista
        listaSeresVivos.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpcionesCRUD(seresVivos[position])
            true
        }

        // Configuración del clic normal sobre un elemento de la lista
        listaSeresVivos.setOnItemClickListener { _, _, position, _ ->
            val serVivo = seresVivos[position]

            // Crear un Intent para abrir una nueva actividad con más detalles
            val intent = Intent(this, GOrganosActivity::class.java).apply {
                putExtra("SER_VIVO_ID", serVivo.id)  // Pasar el ID del ser vivo
                putExtra("NOMBRE", serVivo.nombre)  // Pasar el nombre del ser vivo
                putExtra("TIPO", serVivo.tipo)      // Pasar el tipo del ser vivo
            }

            // Iniciar la actividad
            startActivity(intent)
        }
    }

    // Método para obtener la lista de seres vivos desde la base de datos
    private fun obtenerSeresVivos(): MutableList<SerVivo> {
        val lista = mutableListOf<SerVivo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SerVivo", null)

        // Recorrer los resultados de la consulta y agregar los seres vivos a la lista
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
        return lista
    }

    // Método para mostrar las opciones de CRUD (editar o eliminar) al hacer clic largo en un ser vivo
    private fun mostrarOpcionesCRUD(serVivo: SerVivo) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> editarSerVivo(serVivo)  // Editar el ser vivo
                    1 -> eliminarSerVivo(serVivo)  // Eliminar el ser vivo
                }
            }
            .show()
    }

    // Método para editar un ser vivo
    private fun editarSerVivo(serVivo: SerVivo) {
        val intent = Intent(this, SerVivoEditorActivity::class.java)
        intent.putExtra("SER_VIVO_ID", serVivo.id)  // Pasar el ID del ser vivo para editar
        startActivity(intent)
    }

    // Método para eliminar un ser vivo
    private fun eliminarSerVivo(serVivo: SerVivo) {
        try {
            val db = dbHelper.writableDatabase

            // Eliminar el ser vivo de la base de datos
            val rowsDeleted = db.delete("SerVivo", "id = ?", arrayOf(serVivo.id.toString()))

            if (rowsDeleted > 0) {
                // Eliminar el ser vivo de la lista y actualizar el adaptador
                seresVivos.remove(serVivo)
                cargarListaSeresVivos()

                // Notificar al usuario que el ser vivo fue eliminado correctamente
                Toast.makeText(this, "Ser vivo eliminado con éxito", Toast.LENGTH_SHORT).show()
            } else {
                // Si no se pudo eliminar, mostrar un mensaje de error
                Toast.makeText(this, "Error al eliminar el ser vivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Manejo de excepciones al intentar eliminar
            Log.e("EliminarSerVivo", "Error al eliminar ser vivo", e)
            Toast.makeText(this, "Error al eliminar el ser vivo", Toast.LENGTH_SHORT).show()
        }
    }

    // Método que se ejecuta cuando la actividad vuelve a primer plano
    override fun onResume() {
        super.onResume()
        cargarListaSeresVivos()  // Actualizar la lista de seres vivos
    }

    // Método para cargar la lista de seres vivos y actualizar la interfaz de usuario
    private fun cargarListaSeresVivos() {
        // Obtener la lista actualizada de seres vivos
        seresVivos = obtenerSeresVivos()

        // Crear una lista con los datos formateados de los seres vivos
        val nombres = seresVivos.map {
            "Nombre: ${it.nombre}\n" +
                    "> Tipo: ${it.tipo}\n" +
                    "> Fecha de Nacimiento: ${it.fechaNacimiento}\n" +
                    "> Vertebrado: ${it.esVertebrado}"
        }.toMutableList()

        // Actualizar el adaptador con la nueva lista
        adapter.clear()
        adapter.addAll(nombres)
        adapter.notifyDataSetChanged()
    }
}
