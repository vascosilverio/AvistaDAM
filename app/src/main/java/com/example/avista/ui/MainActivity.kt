package com.example.avista.ui

import android.content.Intent
import android.media.tv.AdResponse
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avista.adapter.ObservacaoListAdapter
import com.example.avista.data.ObservacaoMock
import com.example.avista.databinding.ActivityMainBinding
import com.example.avista.model.Observacao
import com.example.avista.model.ObservacaoGET
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.retrofit.service.ServicoAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val servicoObservacao: ServicoAPI = RetrofitInitializer().servicoAPI()
    private val listaObservacoes = ArrayList<Observacao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //receber o utilizador da atividade de Login
        val utilizador = intent.getStringExtra("utilizador").toString()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnAdicionarObs.setOnClickListener {
            var intent = Intent(this@MainActivity, AdicionarObsActivity::class.java)
            // enviar para a atividade Main o utilizador autenticado
            intent.putExtra("utilizador", utilizador)
            startActivity(intent)
        }



        getObservacoes(utilizador)

    }


    private fun getObservacoes(utilizador: String) {
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
                    }

                    // construir a recycle view com as observacoes do utilizador com autenticação
                    binding.recyclerView.adapter = ObservacaoListAdapter(listaObservacoes, ObservacaoListAdapter.OnClickListener{
                        Toast.makeText(applicationContext,it.data, Toast.LENGTH_SHORT).show()

                })

                }
            }

            override fun onFailure(call: Call<ObservacaoGET>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}