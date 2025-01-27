package com.example.ccgr12024b_wjia

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListaOrganosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var organos: MutableList<Pair<Int, String>> // Ahora incluye IDs
    private lateinit var serVivoTextView: TextView
    private var serVivoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_organos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.listaOrganos)
        serVivoTextView = findViewById(R.id.serVivoTextView) // TextView para SerVivo
        val btnAgregarOrgano = findViewById<Button>(R.id.btnAgregarOrgano)

        serVivoId = intent.getIntExtra("SER_VIVO_ID", -1)

        if (serVivoId == -1) {
            Toast.makeText(this, "Error: no se encontró el ser vivo.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mostrar el nombre del ser vivo
        mostrarSerVivo()

        cargarOrganos(serVivoId)

        btnAgregarOrgano.setOnClickListener {
            val intent = Intent(this, GestionarOrganoActivity::class.java)
            intent.putExtra("SER_VIVO_ID", serVivoId)
            startActivity(intent)
        }

        // Configurar el evento de mantener presionado en el ListView
        listView.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpciones(organos[position].first, organos[position].second)
            true
        }
    }

    private fun mostrarSerVivo() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM SerVivo WHERE id = ?", arrayOf(serVivoId.toString()))
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            serVivoTextView.text = "Ser Vivo: $nombre"
        }
        cursor.close()
    }

    private fun cargarOrganos(serVivoId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre, funcion FROM Organo WHERE ser_vivo_id = ?", arrayOf(serVivoId.toString()))

        organos = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val funcion = cursor.getString(2)
                organos.add(id to "$nombre: $funcion")
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Mostrar los órganos en el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, organos.map { it.second })
        listView.adapter = adapter
    }

    private fun mostrarOpciones(organoId: Int, organoInfo: String) {
        val opciones = arrayOf("Editar", "Eliminar")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones para $organoInfo")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> editarOrgano(organoId)
                1 -> eliminarOrgano(organoId)
            }
        }
        builder.show()
    }

    private fun editarOrgano(organoId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre, funcion, cantidadCelulas, eficiencia FROM Organo WHERE id = ?", arrayOf(organoId.toString()))

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val funcion = cursor.getString(1)
            val cantidadCelulas = cursor.getInt(2)
            val eficiencia = cursor.getDouble(3)

            cursor.close()

            // Abrir la actividad de agregar órgano con los datos existentes
            val intent = Intent(this, GestionarOrganoActivity::class.java)
            intent.putExtra("ORGANO_ID", organoId)
            intent.putExtra("NOMBRE", nombre)
            intent.putExtra("FUNCION", funcion)
            intent.putExtra("CANTIDAD_CELULAS", cantidadCelulas)
            intent.putExtra("EFICIENCIA", eficiencia)
            intent.putExtra("SER_VIVO_ID", serVivoId)
            startActivity(intent)
        } else {
            cursor.close()
            Toast.makeText(this, "Error al cargar los datos del órgano.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarOrgano(organoId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Órgano")
        builder.setMessage("¿Estás seguro de que quieres eliminar este órgano?")
        builder.setPositiveButton("Eliminar") { _, _ ->
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM Organo WHERE id = ?", arrayOf(organoId))
            cargarOrganos(serVivoId)
            Toast.makeText(this, "Órgano eliminado correctamente", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        cargarOrganos(serVivoId)
    }
}