package com.projetodam.avista.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.projetodam.avista.R
import com.projetodam.avista.adapter.ObservacaoListAdapter
import com.projetodam.avista.databinding.ActivityMainBinding
import com.projetodam.avista.model.Observacao
import com.projetodam.avista.model.ObservacaoGET
import com.projetodam.avista.model.ObservacaoSharedModel
import com.projetodam.avista.retrofit.RetrofitInitializer
import com.projetodam.avista.retrofit.service.ServicoAPI
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
*   Atividade Main onde é carregada a RecyclerView com
*   os dados das observações
 */
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    val servicoObservacao: ServicoAPI = RetrofitInitializer().servicoAPI()
    private var listaObservacoes = ArrayList<Observacao>()
    private lateinit var viewModel: ObservacaoSharedModel
    private var observacaoAdapter: ObservacaoListAdapter? = null
    private lateinit var utilizador: String
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // verificar se existem permissões para aceder à localização do utilizador
        if (!checkPermissions()) {
            requestPermissions()
        }
        viewModel = ViewModelProvider(this).get(ObservacaoSharedModel::class.java)
        //receber o utilizador da atividade de Login
        utilizador = intent.getStringExtra("utilizador").toString()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observacaoAdapter = ObservacaoListAdapter(listaObservacoes, applicationContext)
        // colocar o nome de utilizador na view
        binding.txtViewBoasVindas.setText("Olá $utilizador")
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // ordenações
        binding.btnFiltros.setOnClickListener {

            // se a listagem de observações já tiver sido filtrada, voltar a carregar todas as observações do arraylist original
            if(viewModel.listaObservacoesFiltro!!.size < listaObservacoes.size){
                viewModel.listaObservacoesFiltro!!.addAll(listaObservacoes)
            }
            listaObservacoes.clear()
            listaObservacoes.addAll(viewModel.listaObservacoesFiltro!!)
            //observacaoAdapter!!.setObs(listaObservacoes)
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.filtros_observacao)
            dialog.window?.setLayout((0.7 * resources.displayMetrics.widthPixels).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            // obter os objectos da view
            val btnAplicarFiltros = dialog.findViewById<Button>(R.id.btnAplicar)
            val radioGroupData = dialog.findViewById<RadioGroup>(R.id.radioGroupData)
            val filtroEspecie = dialog.findViewById<EditText>(R.id.txtEspecieFiltro)

            // verificar qual a filtragem a aplicar
            btnAplicarFiltros.setOnClickListener {
                val dataAsc = radioGroupData.checkedRadioButtonId == R.id.radioDataAsc

                // se tiver texto no campo de filtragem de espécies, filtrar por texto
                if(!filtroEspecie.text.toString().isEmpty()){
                    observacaoAdapter?.filtrarObs(filtroEspecie.text.toString())
                }

                // ordenar pelo selecionado (Mais antigo ou mais recente)
                observacaoAdapter?.ordenarObs(dataAsc)
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.btnAdicionarObs.setOnClickListener {
            var intent = Intent(this@MainActivity, AdicionarObsActivity::class.java)
            // enviar para a atividade AdicionarObs o utilizador autenticado
            intent.putExtra("utilizador", utilizador)
            intent.putExtra("listaObservacoes", listaObservacoes)
            startActivity(intent)
        }
        getObservacoes(utilizador)
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
    *   Função que permite obter todas as observações da API,
    *   e adiciona cada observação que corresponda ao utilizador
    *   autenticado à lista de observações que vai ser carregada
    *   na ReclycerView
     */
    private fun getObservacoes(utilizador: String) {
        // criar um dialog para mostrar o loading enquanto os dados são enviados para as APIs
        val loading = Dialog(this)
        loading.setContentView(R.layout.loading)
        loading.window?.setLayout((resources.displayMetrics.widthPixels).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        loading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        // usar o Glide para mostrar o GIF com movimento
        val loadingImageView = loading.findViewById<ImageView>(R.id.loadingImageView)
        Glide.with(this)
            .load(R.drawable.loading)
            .into(loadingImageView)
        loading.show()

        // obter as observações da API e filtrar pelo utilizador autenticado
        val call = servicoObservacao.listarObservacoes()
        call.enqueue(object : Callback<ObservacaoGET> {
            override fun onResponse(call: Call<ObservacaoGET>, response: Response<ObservacaoGET>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        for (observacao in it.listaObservacoes){
                            if(observacao.utilizador == utilizador) {
                                // para cada observacao deste utilizador, construir um objecto "Observacao" para adicionar à ArrayList
                                val observacaoCurrente = Observacao(
                                    id = observacao.id,
                                    utilizador = observacao.utilizador,
                                    lat = observacao.lat,
                                    long = observacao.long,
                                    foto = observacao.foto,
                                    descricao = observacao.descricao,
                                    data = observacao.data,
                                    especie = observacao.especie
                                )
                                listaObservacoes.add(observacaoCurrente)
                            }
                        }
                        sendPoints()
                    }
                    construirRecycleView()
                }
                loading.dismiss()
            }

            override fun onFailure(call: Call<ObservacaoGET>, t: Throwable) {
                loading.dismiss()
            }
        })
    }

    /*
    * enviar a lista de observações para o modelo partilhado
    */
    fun sendPoints(){
        viewModel.listaObservacoes=listaObservacoes
    }

    /*
    *   Construir a ReccyclerView com a lista de observações
     */
    fun construirRecycleView(){
        // construir a recycle view com as observacoes do utilizador com autenticação
        binding.recyclerView.adapter = observacaoAdapter
    }
    /*
    * fazer override do método onRestart para quando voltar da actividade de adicionar nova observação, carregar a nova observação também.
     */
    override fun onRestart() {
        super.onRestart()
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}