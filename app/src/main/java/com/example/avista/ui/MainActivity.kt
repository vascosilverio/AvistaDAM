package com.example.avista.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.avista.R
import com.example.avista.adapter.ObservacaoListAdapter
import com.example.avista.databinding.ActivityMainBinding
import com.example.avista.model.Observacao
import com.example.avista.model.ObservacaoGET
import com.example.avista.model.ObservacaoSharedModel
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.retrofit.service.ServicoAPI
import com.example.avista.ui.activity.MapActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable





class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    val servicoObservacao: ServicoAPI = RetrofitInitializer().servicoAPI()
    private var listaObservacoes = ArrayList<Observacao>()
    private lateinit var viewModel: ObservacaoSharedModel
    private var observacaoAdapter: ObservacaoListAdapter? = null
    private lateinit var utilizador: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ObservacaoSharedModel::class.java)

        //receber o utilizador da atividade de Login
        utilizador = intent.getStringExtra("utilizador").toString()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observacaoAdapter = ObservacaoListAdapter(listaObservacoes)

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
            val radioGroupEspecie = dialog.findViewById<RadioGroup>(R.id.radioGroupEspecie)
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
            // enviar para a atividade Main o utilizador autenticado
            intent.putExtra("utilizador", utilizador)
            startActivity(intent)
        }

        getObservacoes(utilizador)
    }

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

        val call = servicoObservacao.listarObservacoes()

        call.enqueue(object : Callback<ObservacaoGET> {
            override fun onResponse(call: Call<ObservacaoGET>, response: Response<ObservacaoGET>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        for (observacao in it.listaObservacoes){
                            Log.d("MainActivity", "utilizador da observacao: ${observacao.utilizador} | utilizador do login: ${utilizador}")
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

    fun sendPoints(){
        viewModel.listaObservacoes=listaObservacoes
    }

    fun construirRecycleView(){
        // construir a recycle view com as observacoes do utilizador com autenticação
        binding.recyclerView.adapter = observacaoAdapter
    }

    // fazer override do método onRestart para quando voltar da actividade de adicionar nova observação, carregar a nova observação também.
    override fun onRestart() {
        super.onRestart()
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}