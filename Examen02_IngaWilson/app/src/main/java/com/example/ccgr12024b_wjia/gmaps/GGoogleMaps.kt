package com.example.ccgr12024b_wjia

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar

class GGoogleMaps : AppCompatActivity() {
    private lateinit var mapa: GoogleMap
    var permisos = false
    val nombrePermisoFine = android.Manifest.permission.ACCESS_FINE_LOCATION
    val nombrePermisoCoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ggoogle_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        solicitarPermisos()
        inicializarLogicaMapa()
        val botonZoo = findViewById<Button>(R.id.btn_ir_zoo)
        botonZoo.setOnClickListener {
            val zoo = LatLng(-0.07087093868627649, -78.35858749118086)
            moverCamaraConZoom(zoo)
        }

    }
    fun tengoPermisos():Boolean{
        val contexto = applicationContext
        val permisoFine = ContextCompat.checkSelfPermission(contexto, nombrePermisoFine)
        val permisoCoarse = ContextCompat.checkSelfPermission(contexto, nombrePermisoCoarse)
        val tienePermisos = permisoFine == PackageManager.PERMISSION_GRANTED &&
                permisoCoarse == PackageManager.PERMISSION_GRANTED
        permisos = tienePermisos
        return permisos
    }
    fun solicitarPermisos(){
        if(!tengoPermisos()){
            ActivityCompat.requestPermissions(
                this, arrayOf(nombrePermisoFine, nombrePermisoCoarse), 1
            )
        }
    }

    fun inicializarLogicaMapa(){
        val fragmentoMapa = supportFragmentManager.
        findFragmentById(R.id.map ) as SupportMapFragment
        fragmentoMapa.getMapAsync { googleMap ->
            with(googleMap){
                mapa = googleMap
                establecerConfiguracionMapa()
                moverZoo()
            }
        }
    }
    fun moverZoo(){
        val guayllabamba = LatLng(-0.07087093868627649, -78.35858749118086)
        val titulo = "Zoológico de Quito"
        val marcadorZoo = anadirMarcador(guayllabamba, titulo)
        marcadorZoo.tag = titulo
        moverCamaraConZoom(guayllabamba)
    }


    fun moverCamaraConZoom(latLang: LatLng, zoom: Float = 17f){
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang,zoom))
    }
    fun anadirMarcador(latLang: LatLng, title: String): Marker {
        return mapa.addMarker(MarkerOptions().position(latLang).title(title))!!
    }

    @SuppressLint("MissingPermission")
    fun establecerConfiguracionMapa(){
        with(mapa){
            if(tengoPermisos()){
                mapa.isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
            }
            uiSettings.isZoomControlsEnabled = true
        }
    }
}