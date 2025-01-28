package com.example.ccgr12024b_wjia


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
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

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaSeresVivos: ListView
    private lateinit var seresVivos: MutableList<SerVivo>
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        listaSeresVivos = findViewById(R.id.listaSeresVivos)

        seresVivos = obtenerSeresVivos()

        // Adaptador con los nombres de los seres vivos
        val nombres = seresVivos.map { "it.nombre" }.toMutableList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listaSeresVivos.adapter = adapter

        findViewById<Button>(R.id.btnCrearSerVivo).setOnClickListener {
            val intent = Intent(this, GestionarSerVivoActivity::class.java)
            startActivity(intent)
        }

        listaSeresVivos.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpcionesCRUD(seresVivos[position])
            true
        }

        listaSeresVivos.setOnItemClickListener { _, _, position, _ ->
            // Obtener el ser vivo seleccionado
            val serVivo = seresVivos[position]

            // Crear un Intent para abrir ListaOrganosActivity
            val intent = Intent(this, ListaOrganosActivity::class.java).apply {
                putExtra("SER_VIVO_ID", serVivo.id)  // Pasar el ID del ser vivo
                putExtra("NOMBRE", serVivo.nombre)  // Pasar el nombre del ser vivo
                putExtra("TIPO", serVivo.tipo)      // Pasar el tipo del ser vivo
            }

            // Iniciar la actividad
            startActivity(intent)
        }

    }

    private fun obtenerSeresVivos(): MutableList<SerVivo> {
        val lista = mutableListOf<SerVivo>()
        val db = dbHelper.readableDatabase
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
        return lista
    }

    private fun mostrarOpcionesCRUD(serVivo: SerVivo) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> editarSerVivo(serVivo)
                    1 -> eliminarSerVivo(serVivo)
                }
            }
            .show()
    }

    private fun editarSerVivo(serVivo: SerVivo) {
        val intent = Intent(this, GestionarSerVivoActivity::class.java)
        intent.putExtra("SER_VIVO_ID", serVivo.id)
        startActivity(intent)
    }

    private fun eliminarSerVivo(serVivo: SerVivo) {
        try {
            val db = dbHelper.writableDatabase

            // Eliminar el ser vivo de la base de datos
            val rowsDeleted = db.delete("SerVivo", "id = ?", arrayOf(serVivo.id.toString()))

            if (rowsDeleted > 0) {
                // Eliminar el ser vivo de la lista y actualizar el adaptador
                seresVivos.remove(serVivo)

                // Actualizar la lista en el adaptador
                cargarListaSeresVivos()  // Esta función actualizará la lista en la interfaz de usuario

                // Notificar al usuario
                Toast.makeText(this, "Ser vivo eliminado con éxito", Toast.LENGTH_SHORT).show()
            } else {
                // Manejo si no se pudo eliminar
                Toast.makeText(this, "Error al eliminar el ser vivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("EliminarSerVivo", "Error al eliminar ser vivo", e)
            Toast.makeText(this, "Error al eliminar el ser vivo", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        cargarListaSeresVivos()
    }

    private fun cargarListaSeresVivos() {
        // Obtén la lista actualizada de seres vivos desde la base de datos
        seresVivos = obtenerSeresVivos()

        // Actualiza los datos del adaptador
        val nombres = seresVivos.map { "Nombre: ${it.nombre}\n" +
                "> Tipo: ${it.tipo}\n" +
                "> Fecha de Nacimiento: ${it.fechaNacimiento}\n" +
                "> Vertebrado: ${it.esVertebrado}" }.toMutableList()
        adapter.clear()
        adapter.addAll(nombres)
        adapter.notifyDataSetChanged()
    }

}
