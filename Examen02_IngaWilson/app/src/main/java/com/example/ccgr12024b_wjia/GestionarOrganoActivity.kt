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
import java.time.LocalDate

class GestionarOrganoActivity : AppCompatActivity() {

    private var serVivoId: Int = -1 // ID del SerVivo asociado
    private var organoId: Int = -1 // ID del órgano (para edición)
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvSerVivoInfo: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionar_organo)

        // Configuración visual dinámica
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        tvSerVivoInfo = findViewById(R.id.tvSerVivoInfo)

        val etNombre = findViewById<EditText>(R.id.etNombreOrgano)
        val etFuncion = findViewById<EditText>(R.id.etFuncionOrgano)
        val etCantidadCelulas = findViewById<EditText>(R.id.etCantidadCelulas)
        val etEficiencia = findViewById<EditText>(R.id.etEficiencia)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarOrgano)

        // Obtener IDs de SerVivo y Organo
        serVivoId = intent.getIntExtra("SER_VIVO_ID", -1)
        if (serVivoId == -1) {
            Toast.makeText(this, "Error: no se encontró el ser vivo.", Toast.LENGTH_SHORT).show()
            finish()  // Termina la actividad si no se encontró el ID
            return
        }

        organoId = intent.getIntExtra("ORGANO_ID", -1)

        if (serVivoId == -1) {
            Toast.makeText(this, "Error: no se encontró el ser vivo asociado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mostrar información del SerVivo
        //mostrarInfoSerVivo()

        // Si es edición, cargar datos del órgano
        if (organoId != -1) {
            cargarDatosOrgano(organoId, etNombre, etFuncion, etCantidadCelulas, etEficiencia)
        }

        // Configurar el botón "Guardar"
        btnGuardar.setOnClickListener {
            guardarOrgano(
                etNombre.text.toString(),
                etFuncion.text.toString(),
                etCantidadCelulas.text.toString(),
                etEficiencia.text.toString()
            )
        }
    }

    private fun mostrarInfoSerVivo() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre, tipo, esVertebrado, fechaNacimiento FROM SerVivo WHERE id = ?",
            arrayOf(serVivoId.toString())
        )

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val tipo = cursor.getString(1)
            val esVertebrado = cursor.getInt(2) == 1
            val fechaNacimiento = cursor.getString(3)
            tvSerVivoInfo.text = "$nombre - $tipo - ${if (esVertebrado) "Vertebrado" else "Invertebrado"} - $fechaNacimiento"
        }
        cursor.close()
    }

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
            etNombre.setText(cursor.getString(0))
            etFuncion.setText(cursor.getString(1))
            etCantidadCelulas.setText(cursor.getInt(2).toString())
            etEficiencia.setText(cursor.getDouble(3).toString())
        }
        cursor.close()
    }

    private fun guardarOrgano(
        nombre: String,
        funcion: String,
        cantidadCelulas: String,
        eficiencia: String
    ) {
        if (nombre.isEmpty() || funcion.isEmpty() || cantidadCelulas.isEmpty() || eficiencia.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidadCelulasInt = cantidadCelulas.toIntOrNull()
        val eficienciaDouble = eficiencia.toDoubleOrNull()

        if (cantidadCelulasInt == null || eficienciaDouble == null) {
            Toast.makeText(this, "Por favor, ingresa valores válidos.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("funcion", funcion)
            put("cantidadCelulas", cantidadCelulasInt)
            put("eficiencia", eficienciaDouble)
            put("servivo_id", serVivoId)
        }

        if (organoId == -1) {
            // Agregar nuevo órgano
            val resultado = db.insert("Organo", null, valores)
            if (resultado != -1L) {
                Toast.makeText(this, "Órgano agregado correctamente.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el órgano.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Editar órgano existente
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