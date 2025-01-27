package com.example.ccgr12024b_wjia


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        val nombres = seresVivos.map { "${it.nombre} - ${it.tipo}" }.toMutableList()
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
    }

    private fun obtenerSeresVivos(): MutableList<SerVivo> {
        val lista = mutableListOf<SerVivo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SerVivo", null)

        val formatter = DateTimeFormatter.ISO_DATE
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                val esVertebrado = cursor.getInt(cursor.getColumnIndexOrThrow("esVertebrado")) == 1
                val fechaNacimiento = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento")), formatter)

                // Obtener órganos asociados
                val organos = dbHelper.obtenerOrganosPorSerVivo(id)

                lista.add(SerVivo(id, nombre, tipo, esVertebrado, fechaNacimiento, organos))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    private fun mostrarOpcionesCRUD(serVivo: SerVivo) {
        val opciones = arrayOf("Editar", "Eliminar", "Ver Órganos")
        AlertDialog.Builder(this)
            .setTitle("Opciones para ${serVivo.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> editarSerVivo(serVivo)
                    1 -> eliminarSerVivo(serVivo)
                    2 -> verOrganos(serVivo)
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
        val db = dbHelper.writableDatabase
        db.delete("SerVivo", "id = ?", arrayOf(serVivo.id.toString()))
        seresVivos.remove(serVivo)
        adapter.remove("${serVivo.nombre} - ${serVivo.tipo}")
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        cargarListaSeresVivos()
    }

    private fun cargarListaSeresVivos() {
        // Obtén la lista actualizada de seres vivos desde la base de datos
        seresVivos = obtenerSeresVivos()

        // Actualiza los datos del adaptador
        val nombres = seresVivos.map { "${it.nombre} - ${it.tipo}" }.toMutableList()
        adapter.clear()
        adapter.addAll(nombres)
        adapter.notifyDataSetChanged()
    }

    private fun verOrganos(serVivo: SerVivo) {
        Log.d("MainActivity", "ID del ser vivo seleccionado: ${serVivo.id}")

        val intent = Intent(this, ListaOrganosActivity::class.java)
        intent.putExtra("NOMBRE", serVivo.nombre)
        intent.putExtra("TIPO", serVivo.tipo)
        intent.putExtra("SER_VIVO_ID", serVivo.id)
        startActivity(intent)
    }
}
