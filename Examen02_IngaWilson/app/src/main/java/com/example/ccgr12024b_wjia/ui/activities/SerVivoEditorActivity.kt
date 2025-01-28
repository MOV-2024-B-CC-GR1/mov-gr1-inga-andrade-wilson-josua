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

    private var serVivoId: Int? = null  // ID del SerVivo para edición
    private lateinit var etNombre: EditText
    private lateinit var etTipo: EditText
    private lateinit var etEsVertebrado: CheckBox
    private lateinit var etFechaNacimiento: EditText
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: DatabaseHelper  // Instancia de la clase DatabaseHelper para acceder a la base de datos

    // Método que se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Activar la visualización en pantalla completa
        setContentView(R.layout.activity_gestionar_ser_vivo)  // Establecer el layout de la actividad

        // Inicializar las vistas
        etNombre = findViewById(R.id.etNombre)
        etTipo = findViewById(R.id.etTipo)
        etEsVertebrado = findViewById(R.id.cbEsVertebrado)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        btnGuardar = findViewById(R.id.btnGuardarSerVivo)

        // Inicializar la base de datos
        dbHelper = DatabaseHelper(this)

        // Detectar si estamos en modo de edición
        serVivoId = intent.getIntExtra("SER_VIVO_ID", -1).takeIf { it != -1 }

        if (serVivoId != null) {
            // Si estamos en modo edición, cargar los datos del SerVivo
            cargarDatosSerVivo(serVivoId!!)
        }

        // Configuración del botón de guardar
        btnGuardar.setOnClickListener {
            // Obtener los valores ingresados por el usuario
            val nombre = etNombre.text.toString()
            val tipo = etTipo.text.toString()
            val esVertebrado = etEsVertebrado.isChecked
            val fechaNacimiento = etFechaNacimiento.text.toString()

            // Validar que los campos no estén vacíos
            if (nombre.isNotEmpty() && tipo.isNotEmpty() && fechaNacimiento.isNotEmpty()) {
                if (serVivoId != null) {
                    // Si es un modo de edición, actualizar el SerVivo
                    actualizarSerVivo(serVivoId!!)
                } else {
                    // Si es nuevo, agregar el SerVivo
                    agregarSerVivo(nombre, tipo, esVertebrado, fechaNacimiento)
                }
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuración visual dinámica para manejar márgenes de los dispositivos con pantallas más grandes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Cargar los datos de un SerVivo específico para la edición
    private fun cargarDatosSerVivo(id: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SerVivo WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
            val esVertebrado = cursor.getInt(cursor.getColumnIndexOrThrow("esVertebrado")) == 1
            val fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaNacimiento"))

            // Mostrar los datos del SerVivo en los campos de edición
            etNombre.setText(nombre)
            etTipo.setText(tipo)
            etEsVertebrado.isChecked = esVertebrado
            etFechaNacimiento.setText(fechaNacimiento)
        }
        cursor.close()
    }

    // Actualizar los datos de un SerVivo existente en la base de datos
    private fun actualizarSerVivo(id: Int) {
        val db = dbHelper.writableDatabase

        // Obtener los valores de los campos de edición
        val nombre = etNombre.text.toString()
        val tipo = etTipo.text.toString()
        val esVertebrado = if (etEsVertebrado.isChecked) 1 else 0
        val fechaNacimiento = etFechaNacimiento.text.toString()

        // Crear un objeto ContentValues para almacenar los nuevos datos
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("esVertebrado", esVertebrado)
            put("fechaNacimiento", fechaNacimiento)
        }

        // Actualizar el registro del SerVivo en la base de datos
        val filasActualizadas = db.update("SerVivo", valores, "id = ?", arrayOf(id.toString()))

        if (filasActualizadas > 0) {
            Toast.makeText(this, "Ser Vivo actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()  // Cerrar la actividad
        } else {
            Toast.makeText(this, "Error al actualizar el Ser Vivo", Toast.LENGTH_SHORT).show()
        }
    }

    // Agregar un nuevo SerVivo a la base de datos
    private fun agregarSerVivo(
        nombre: String,
        tipo: String,
        esVertebrado: Boolean,
        fechaNacimiento: String
    ) {
        val db = dbHelper.writableDatabase

        // Crear un objeto ContentValues con los datos del nuevo SerVivo
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("esVertebrado", if (esVertebrado) 1 else 0)
            put("fechaNacimiento", fechaNacimiento)
        }

        // Insertar el nuevo SerVivo en la base de datos
        val resultado = db.insert("SerVivo", null, valores)
        if (resultado != -1L) {
            Toast.makeText(this, "Ser Vivo agregado exitosamente", Toast.LENGTH_SHORT).show()
            finish()  // Cerrar la actividad
        } else {
            Toast.makeText(this, "Error al agregar el Ser Vivo", Toast.LENGTH_SHORT).show()
        }
    }
}
