package com.example.ccgr12024b_wjia

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SerVivoEditorActivity : AppCompatActivity() {

    private var serVivoId: Int? = null
    private lateinit var etNombre: EditText
    private lateinit var etTipo: EditText
    private lateinit var etEsVertebrado: CheckBox
    private lateinit var etFechaNacimiento: EditText
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_ser_vivo)

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        etTipo = findViewById(R.id.etTipo)
        etEsVertebrado = findViewById(R.id.cbEsVertebrado)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        btnGuardar = findViewById(R.id.btnGuardarSerVivo)

        dbHelper = DatabaseHelper(this)

        // Detectar si estamos en modo edición
        serVivoId = intent.getIntExtra("SER_VIVO_ID", -1).takeIf { it != -1 }

        if (serVivoId != null) {
            // Modo edición: cargar datos
            cargarDatosSerVivo(serVivoId!!)
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val tipo = etTipo.text.toString()
            val esVertebrado = etEsVertebrado.isChecked
            val fechaNacimiento = etFechaNacimiento.text.toString()

            if (nombre.isNotEmpty() && tipo.isNotEmpty() && fechaNacimiento.isNotEmpty()) {
                if (serVivoId != null) {
                    // Editar el ser vivo
                    actualizarSerVivo(serVivoId!!)
                } else {
                    // Crear un nuevo ser vivo
                    agregarSerVivo(nombre, tipo, esVertebrado, fechaNacimiento)
                }
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarDatosSerVivo(id: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SerVivo WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
            val esVertebrado = cursor.getInt(cursor.getColumnIndexOrThrow("esVertebrado")) == 1
            val fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento"))

            // Mostrar los datos en los campos de texto
            etNombre.setText(nombre)
            etTipo.setText(tipo)
            etEsVertebrado.isChecked = esVertebrado
            etFechaNacimiento.setText(fechaNacimiento)
        }
        cursor.close()
    }

    private fun actualizarSerVivo(id: Int) {
        val db = dbHelper.writableDatabase

        val nombre = etNombre.text.toString()
        val tipo = etTipo.text.toString()
        val esVertebrado = if (etEsVertebrado.isChecked) 1 else 0
        val fechaNacimiento = etFechaNacimiento.text.toString()

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("esVertebrado", esVertebrado)
            put("fechaNacimiento", fechaNacimiento)
        }

        val filasActualizadas = db.update("SerVivo", valores, "id = ?", arrayOf(id.toString()))

        if (filasActualizadas > 0) {
            Toast.makeText(this, "Ser Vivo actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish() // Regresar al MainActivity
        } else {
            Toast.makeText(this, "Error al actualizar el Ser Vivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun agregarSerVivo(
        nombre: String,
        tipo: String,
        esVertebrado: Boolean,
        fechaNacimiento: String
    ) {
        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("esVertebrado", if (esVertebrado) 1 else 0)
            put("fechaNacimiento", fechaNacimiento)
        }

        val resultado = db.insert("SerVivo", null, valores)
        if (resultado != -1L) {
            Toast.makeText(this, "Ser Vivo agregado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al agregar el Ser Vivo", Toast.LENGTH_SHORT).show()
        }
    }
}
