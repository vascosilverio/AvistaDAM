package com.projetodam.avista.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.projetodam.avista.R
import com.projetodam.avista.model.Observacao
import com.projetodam.avista.ui.BaseActivity
import com.squareup.picasso.Picasso
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

/*
* Classe usada para construir o mapa.
* Esta classe é usada para o mapa de adicionar e editar novas observações, como também no mapa
* de visualizar todas as observações do utilizador.
 */
class MapActivity : BaseActivity() {
    private lateinit var mapView: MapView
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var listaObservacoes = ArrayList<Observacao>()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var mapEventsOverlay: MapEventsOverlay
    lateinit var latitude: String
    lateinit var longitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapview)

        // obter a lista de observações enviada por extra no intent
        listaObservacoes = intent.getSerializableExtra("listaObservacoes") as ArrayList<Observacao>

        // verificar através da chave "option" recebida por extra no intent, se esta classe
        // foi chamada através da atividade de adicionar observações, da classe de editar observações,
        // ou da classe de listagem de todos os marcadores do utilizador
        val option = intent.getStringExtra("option")
        when (option) {
            "OPTION_1" -> {
                val view = findViewById<LinearLayout>(R.id.guardarCoords)
                view.visibility = View.GONE
                val textViewToChange = findViewById<TextView>(R.id.textoInferior)
                textViewToChange.text = "Clique no marcador para abrir os detalhes."
            }
            "OPTION_2" -> {
                val view = findViewById<LinearLayout>(R.id.guardarCoords)
                view.visibility = View.VISIBLE
                val textViewToChange = findViewById<TextView>(R.id.textoInferior)
                textViewToChange.text = "Coloque o marcador no sítion onde observou a ave."
            }
            "OPTION_3" -> {
                val view = findViewById<LinearLayout>(R.id.guardarCoords)
                view.visibility = View.VISIBLE
                val textViewToChange = findViewById<TextView>(R.id.textoInferior)
                textViewToChange.text = "Coloque o marcador no sítion onde observou a ave."
            }
        }

        // obter a latitude e longitude que foram lidas na actividade de adicionar observações
        latitude = intent.getStringExtra("latitude")!!.toString()
        longitude = intent.getStringExtra("longitude")!!.toString()
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, getSharedPreferences("osmdroid", 0))
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.isClickable = true

        // adicionar o marcador com as posições recebidas da atividade anterior e centrar o mapa nesse ponto
        val localizacao = GeoPoint(latitude.toDouble(), longitude.toDouble())
        atualizarMarcador(localizacao)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(localizacao)

        // verificar se existem permissões para aceder à localização do utilizador
        if (checkPermissions()) {
            setupLocationListener()
        } else {
            requestPermissions()
        }

        // colocar o marcador onde o utilizador clicar
        mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                atualizarMarcador(p)
                return false
            }
            override fun longPressHelper(p: GeoPoint): Boolean {
                atualizarMarcador(p)
                return false
            }
        })
        mapView.overlays.add(mapEventsOverlay)

        // adicionar o marcador com as posições recebidas da atividade anterior e centrar o mapa nesse ponto
        val btnObterCoordenadas = findViewById<Button>(R.id.btnObterCoordenadas)
        btnObterCoordenadas.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitudeAtualizada", latitude)
            intent.putExtra("longitudeAtualizada", longitude)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        if(option=="OPTION_1") populateMap(mapView,listaObservacoes)
    }

    /*
    *   Função para atualizar a posição do novo marcador
     */
    private fun atualizarMarcador(posicao: GeoPoint) {
        val marcador = Marker(mapView)
        marcador.position = posicao
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        latitude = marcador.position.latitude.toString()
        longitude = marcador.position.longitude.toString()
        // limpar o marcador anterior
        mapView.overlays.removeAll { it is Marker }
        // adicionar o novo marcador
        mapView.overlays.add(marcador)
    }

    /*
    * Obter as coordenadas atuais de GPS
     */
    private fun setupLocationListener() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mapView.controller.setCenter(GeoPoint(location.latitude, location.longitude))
                locationManager.removeUpdates(this)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // verificar se existem permissões para usar o GPS
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    /*
    *   Verificar as permissões para o GPS
     */
    private fun checkPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationPermission && coarseLocationPermission
    }

    /*
    * Solicitar permissões ao utilizador
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    /*
    * popular o mapa com todas as observações do utilizador
     */
    fun populateMap(mapView: MapView, observacoes: ArrayList<Observacao>) {
        // limpar marcadores que possam existir no mapa
        mapView.overlays.clear()

        // obter o controller do mapa para poder efetuar zoom
        val mapController: IMapController = mapView.controller

        // iterar todas as observações para colocar todos os marcadores
        for (observacao in observacoes) {
            val latitude = observacao.lat
            val longitude = observacao.long
            val fotoPath = observacao.foto
            val nomeAve = observacao.especie
            val descricao = observacao.descricao
            val data = observacao.data
            val geoPoint = latitude?.toDouble()?.let { longitude?.toDouble()?.let { it1 -> GeoPoint(it, it1) } }

            // criar um marcador para cada observação
            val marker = Marker(mapView)
            marker.position = geoPoint
            marker.title = nomeAve + "\n" + descricao + "\n" + data
            val imageView = ImageView(this)
            Picasso.get().load(fotoPath).into(imageView)
            val drawable : Drawable? = imageView?.drawable
            if (fotoPath != null && fotoPath.isNotEmpty()) {
                marker.image = drawable
            }
            // colocar o marcador no mapa
            mapView.overlays.add(marker)
        }

        // atualizar o mapa
        mapView.invalidate()
    }

}