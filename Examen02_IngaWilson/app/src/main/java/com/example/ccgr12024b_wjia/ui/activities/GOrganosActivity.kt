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
    private lateinit var organos: MutableList<Pair<Int, String>>
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

        // Obtener el ID del ser vivo y otros datos
        serVivoId = intent.getIntExtra("SER_VIVO_ID", -1)
        val nombre = intent.getStringExtra("NOMBRE")
        val tipo = intent.getStringExtra("TIPO")

        if (serVivoId == -1) {
            Toast.makeText(this, "Error: no se encontró el ser vivo.", Toast.LENGTH_SHORT).show()
            finish()  // Termina la actividad si no se encontró el ID
            return
        }

        // Mostrar el nombre y tipo del ser vivo
        serVivoTextView.text = "Ser Vivo: $nombre"

        // Cargar los órganos del ser vivo
        cargarOrganos(serVivoId)

        // Botón para agregar un nuevo órgano
        btnAgregarOrgano.setOnClickListener {
            val intent = Intent(this, OrganoEditorActivity::class.java)
            intent.putExtra("SER_VIVO_ID", serVivoId)
            startActivity(intent)
        }

        // Configura el evento de clic en el ListView para mostrar más detalles del órgano
        listView.setOnItemClickListener { _, _, position, _ ->
            val organoId = organos[position].first  // ID del órgano
            val organoNombre = organos[position].second  // Nombre y función del órgano
            Toast.makeText(this, "Información: $organoNombre", Toast.LENGTH_LONG).show()
        }

        // Configurar el evento de mantener presionado en el ListView
        listView.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpciones(organos[position].first, organos[position].second)
            true
        }
    }

    // Método para cargar los órganos de la base de datos
    private fun cargarOrganos(serVivoId: Int) {
        val db = dbHelper.readableDatabase
        // Obtener el nombre del ser vivo
        val serVivoCursor = db.rawQuery("SELECT nombre FROM SerVivo WHERE id = ?", arrayOf(serVivoId.toString()))
        var nombreSerVivo = "Ser Vivo Desconocido"

        if (serVivoCursor.moveToFirst()) {
            nombreSerVivo = serVivoCursor.getString(0)
        }
        serVivoCursor.close()
        val cursor = db.rawQuery("SELECT id, nombre, funcion, cantidadCelulas, eficiencia FROM Organo WHERE serVivo_id = ?", arrayOf(serVivoId.toString()))

        organos = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val funcion = cursor.getString(2)
                val cantidadCelulas = cursor.getInt(3)
                val eficiencia = cursor.getDouble(4)
                organos.add(id to "$nombre:\n " +
                        "> Función: $funcion\n " +
                        "> Cantidad de Células: $cantidadCelulas\n " +
                        "> Eficiencia: $eficiencia")
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Mostrar los órganos en el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, organos.map { it.second })
        listView.adapter = adapter
    }

    // Mostrar un cuadro de opciones (Editar o Eliminar) para un órgano
    private fun mostrarOpciones(organoId: Int, organoInfo: String) {
        val opciones = arrayOf("Editar", "Eliminar")

        val builder = AlertDialog.Builder(this)

        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> editarOrgano(organoId)
                1 -> eliminarOrgano(organoId)
            }
        }
        builder.show()
    }

    // Método para editar un órgano
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
            val intent = Intent(this, OrganoEditorActivity::class.java)
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

    // Método para eliminar un órgano
    private fun eliminarOrgano(organoId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Órgano")
        builder.setMessage("¿Estás seguro de que quieres eliminar este órgano?")
        builder.setPositiveButton("Eliminar") { _, _ ->
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM Organo WHERE id = ?", arrayOf(organoId))
            cargarOrganos(serVivoId)  // Recargar la lista de órganos
            Toast.makeText(this, "Órgano eliminado correctamente", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    // Recargar la lista de órganos cuando la actividad se reanude
    override fun onResume() {
        super.onResume()
        cargarOrganos(serVivoId)
    }
}
