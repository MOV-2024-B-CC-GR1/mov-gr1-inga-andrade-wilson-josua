package com.example.ccgr12024b_wjia

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrganoEditorActivity : AppCompatActivity() {

    // Variables para almacenar los IDs y la referencia al helper de la base de datos
    private var serVivoId: Int = -1 // ID del SerVivo asociado
    private var organoId: Int = -1 // ID del órgano (para edición)
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvSerVivoInfo: TextView

    // Método de creación de la actividad
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilitar los bordes extendidos
        setContentView(R.layout.activity_gestionar_organo)

        // Configuración visual dinámica para ajustar el padding de la vista principal según el sistema de barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización del helper de la base de datos y las vistas
        dbHelper = DatabaseHelper(this)
        tvSerVivoInfo = findViewById(R.id.tvSerVivoInfo)

        val etNombre = findViewById<EditText>(R.id.etNombreOrgano)
        val etFuncion = findViewById<EditText>(R.id.etFuncionOrgano)
        val etCantidadCelulas = findViewById<EditText>(R.id.etCantidadCelulas)
        val etEficiencia = findViewById<EditText>(R.id.etEficiencia)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarOrgano)

        // Obtener IDs de SerVivo y Organo desde los extras del Intent
        serVivoId = intent.getIntExtra("SER_VIVO_ID", -1)
        if (serVivoId == -1) {
            Toast.makeText(this, "Error: no se encontró el ser vivo.", Toast.LENGTH_SHORT).show()
            finish()  // Termina la actividad si no se encontró el ID
            return
        }

        organoId = intent.getIntExtra("ORGANO_ID", -1)

        // Verifica que el ID de SerVivo sea válido
        if (serVivoId == -1) {
            Toast.makeText(this, "Error: no se encontró el ser vivo asociado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Si es edición, carga los datos del órgano existente
        if (organoId != -1) {
            cargarDatosOrgano(organoId, etNombre, etFuncion, etCantidadCelulas, etEficiencia)
        }

        // Configura el botón "Guardar" para almacenar los cambios en la base de datos
        btnGuardar.setOnClickListener {
            guardarOrgano(
                etNombre.text.toString(),
                etFuncion.text.toString(),
                etCantidadCelulas.text.toString(),
                etEficiencia.text.toString()
            )
        }
    }

    // Método para cargar los datos de un órgano existente en los campos de texto
    private fun cargarDatosOrgano(
        organoId: Int,
        etNombre: EditText,
        etFuncion: EditText,
        etCantidadCelulas: EditText,
        etEficiencia: EditText
    ) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre, funcion, cantidadCelulas, eficiencia FROM Organo WHERE id = ?",
            arrayOf(organoId.toString())
        )

        if (cursor.moveToFirst()) {
            // Asigna los valores recuperados a los campos de edición
            etNombre.setText(cursor.getString(0))
            etFuncion.setText(cursor.getString(1))
            etCantidadCelulas.setText(cursor.getInt(2).toString())
            etEficiencia.setText(cursor.getDouble(3).toString())
        }
        cursor.close()
    }

    // Método para guardar o editar un órgano en la base de datos
    private fun guardarOrgano(
        nombre: String,
        funcion: String,
        cantidadCelulas: String,
        eficiencia: String
    ) {
        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || funcion.isEmpty() || cantidadCelulas.isEmpty() || eficiencia.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar que los valores de cantidad de células y eficiencia sean válidos
        val cantidadCelulasInt = cantidadCelulas.toIntOrNull()
        val eficienciaDouble = eficiencia.toDoubleOrNull()

        if (cantidadCelulasInt == null || eficienciaDouble == null) {
            Toast.makeText(this, "Por favor, ingresa valores válidos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Preparar los valores para ser insertados o actualizados en la base de datos
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("funcion", funcion)
            put("cantidadCelulas", cantidadCelulasInt)
            put("eficiencia", eficienciaDouble)
            put("servivo_id", serVivoId)
        }

        // Si no hay organoId, es un nuevo órgano, de lo contrario es una edición
        if (organoId == -1) {
            // Insertar un nuevo órgano
            val resultado = db.insert("Organo", null, valores)
            if (resultado != -1L) {
                Toast.makeText(this, "Órgano agregado correctamente.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el órgano.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Editar un órgano existente
            val resultado = db.update("Organo", valores, "id = ?", arrayOf(organoId.toString()))
            if (resultado > 0) {
                Toast.makeText(this, "Órgano editado correctamente.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al editar el órgano.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
