package com.example.avista.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.avista.R
import com.example.avista.ui.BaseActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


class MapActivity : BaseActivity() {
    private lateinit var mapView: MapView
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var mapEventsOverlay: MapEventsOverlay
    lateinit var latitude: String
    lateinit var longitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapview)

        // obter a latitude e longitude que foram lidas na actividade de adicionar observações
        latitude = intent.getStringExtra("latitude").toString()
        longitude = intent.getStringExtra("longitude").toString()

        Log.d("Latitude: ", latitude)
        Log.d("Longitude: ", longitude)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, getSharedPreferences("osmdroid", 0))

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.isClickable = true

        val localizacao = GeoPoint(latitude.toDouble(), longitude.toDouble())

        // adicionar o marcador com as posições recebidas da atividade anterior e centrar o mapa nesse ponto
        atualizarMarcador(localizacao)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(localizacao)

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

        val btnObterCoordenadas = findViewById<Button>(R.id.btnObterCoordenadas)
        btnObterCoordenadas.setOnClickListener {
            // atualizar a latitude e longitude na atividade anterior
            val intent = Intent()
            intent.putExtra("latitudeAtualizada", latitude)
            intent.putExtra("longitudeAtualizada", longitude)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun atualizarMarcador(posicao: GeoPoint) {
        // atualizar a posição do marcador
        val marcador = Marker(mapView)
        marcador.position = posicao
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        latitude = marcador.position.latitude.toString()
        longitude = marcador.position.longitude.toString()
        Log.d("NOVA LATITUDE, LONGITUDE","$latitude, $longitude")
        mapView.overlays.removeAll { it is Marker }  // limpar o marcador anterior
        mapView.overlays.add(marcador)
    }

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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private fun checkPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationPermission && coarseLocationPermission
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
}