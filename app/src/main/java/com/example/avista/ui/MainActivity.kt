package com.example.avista.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.avista.R
import com.example.avista.adapter.ObservacaoListAdapter
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

        binding.btnLogout.setOnClickListener{
            val sharedPref = getSharedPreferences("utilizador", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
            finish()
        }

        getObservacoes(utilizador)

    }


    private fun getObservacoes(utilizador: String) {
        // criar um dialog para mostrar o loading enquanto os dados são enviados para as APIs
        val loading = Dialog(this)
        loading.setContentView(R.layout.loading)
        loading.window?.setLayout((resources.displayMetrics.widthPixels * 0.5).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
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
                loading.dismiss()
            }

            override fun onFailure(call: Call<ObservacaoGET>, t: Throwable) {
                loading.dismiss()
            }
        })
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