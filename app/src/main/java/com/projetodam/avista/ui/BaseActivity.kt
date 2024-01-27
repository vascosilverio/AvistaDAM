package com.projetodam.avista.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.projetodam.avista.R
import com.projetodam.avista.model.Observacao
import com.projetodam.avista.model.ObservacaoSharedModel
import com.projetodam.avista.ui.activity.MapActivity

/*
* Classe Activity que extende a AppCompatActivity para implementação da ActionBar nas restantes atividades
 */
open class BaseActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_CODE = 102
    private lateinit var utilizador: String
    private var listaObservacoes = ArrayList<Observacao>()
    private lateinit var viewModel: ObservacaoSharedModel
    private val PICK_MARKER_CODE = 103
    var latitudeB = 0.0
    var longitudeB = 0.0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.bottom_nav, menu)
        viewModel = ViewModelProvider(this).get(ObservacaoSharedModel::class.java)
        localizacaoAtual()
        return true
    }

    /*
    * associa aos items da action bar os eventos de clique
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //abre o mapa com a lista de observaçoes do utilizador
            R.id.item_mapa -> {
                utilizador = intent.getStringExtra("utilizador").toString()
                // envia a lista de observações do SharedView Model
                try {
                    listaObservacoes = viewModel.listaObservacoes!!
                } catch (e: Exception){
                    Log.e(e.message, "Erro a tentar obter a lista de observações")
                    listaObservacoes = viewModel.listaObservacoesFiltro!!
                }
                var intent = Intent(this, MapActivity::class.java)
                // envia para a atividade Mapa a latitude e longitude atuais
                intent.putExtra("latitude", latitudeB.toString())
                intent.putExtra("longitude", longitudeB.toString())
                intent.putExtra("utilizador", utilizador)
                intent.putExtra("listaObservacoes", listaObservacoes)
                intent.putExtra("option", "OPTION_1")
                startActivityForResult(intent, PICK_MARKER_CODE)
                true
            }
            //abre a RecyclerView de observações
            R.id.item_observacoes -> {
                //recebe o utilizador da atividade de Login
                utilizador = intent.getStringExtra("utilizador").toString()
                var intent = Intent(this@BaseActivity, MainActivity::class.java)
                // envia para a atividade Main o utilizador autenticado
                intent.putExtra("utilizador", utilizador)
                startActivity(intent)
                true
            }
            // executa o logout do utilizador autenticado
            R.id.item_Logout ->
                {
                // limpa o login guardado na sharedPreferences
                val sharedPref = getSharedPreferences("utilizadorAutenticado", MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            //abre a atividade e view de About/Info
            R.id.item_Info -> {
                utilizador = intent.getStringExtra("utilizador").toString()
                var intent = Intent(this@BaseActivity, AboutActivity::class.java)
                // envia para a atividade Main o utilizador autenticado
                intent.putExtra("utilizador", utilizador)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*
    * inicia a atividade correspondente ao request code enviado
    * as atividades são captura de foto e obtenção
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    localizacaoAtual()
                } else {
                    Toast.makeText(this, "É preciso dar permissão de acesso à localização para obter a latitude e longitude.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*
    * obtém e atualiza a localização atual do gps
    */
    @SuppressLint("ServiceCast")
    private fun localizacaoAtual() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val gps: String = LocationManager.GPS_PROVIDER

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val localizacao: Location? = locationManager.getLastKnownLocation(gps)

            if (localizacao != null) {
                latitudeB = localizacao.latitude
                longitudeB = localizacao.longitude
            } else {
                Log.e("AdicionarObsActivity", "Erro a obter a localização.")
            }
        }
    }
}