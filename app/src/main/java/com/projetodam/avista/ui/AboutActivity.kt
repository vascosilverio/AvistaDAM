package com.projetodam.avista.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.projetodam.avista.R
import com.projetodam.avista.databinding.AboutBinding
import com.projetodam.avista.model.Observacao
import com.projetodam.avista.model.ObservacaoGET
import com.projetodam.avista.model.RespostaAPI
import com.projetodam.avista.model.UtilizadorGET
import com.projetodam.avista.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Thread.sleep

/*
* Classe Activity que extende a BaseActivity e apresenta a página de About
* Implementa o botão de remover conta e dados do utilizador autenticado
 */
class AboutActivity : BaseActivity() {

    private lateinit var binding: AboutBinding
    private lateinit var utilizador: String
    private lateinit var listaObs: List<Observacao>
    lateinit var loading: Dialog

        /*
        * Apresenta o layout about_xml
        * Associa os eventos no botão de remover conta e cria uma imagem de loading para aguardar a remoção dos dados do utilizador
         */
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // recebe o utilizador da atividade anterior
        utilizador = intent.getStringExtra("utilizador").toString()

        // botão para remover a observação que foi selecionada
        binding.btnRemoverConta.setOnClickListener {
            // é preciso confirmar o dialog para certificar que o utilizador pretende mesmo remover a observação
            val dialogConfirmacao = AlertDialog.Builder(this)
            dialogConfirmacao.setTitle("Remover conta de utilizador")
            dialogConfirmacao.setMessage("Tem a certeza de que pretende remover a sua conta de utilizador e todas as suas observações?")
            dialogConfirmacao.setPositiveButton("Sim") { dialog, which ->
                loading = Dialog(this)
                loading.setContentView(R.layout.loading)
                loading.window?.setLayout(
                    (resources.displayMetrics.widthPixels).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                loading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading.setCancelable(false)
                // usa o Glide para mostrar o GIF com movimento
                val loadingImageView = loading.findViewById<ImageView>(R.id.loadingImageView)
                Glide.with(this)
                    .load(R.drawable.loading)
                    .into(loadingImageView)
                loading.show()
                removerConta(utilizador)
            }
            dialogConfirmacao.setNegativeButton("Não", null)
            dialogConfirmacao.show()
        }


    }

    /*
    * executa a remoção da conta e dados do utilizador
     */
    private fun removerConta(user: String) {
        val call = RetrofitInitializer().servicoAPI().listarUtilizadores()
        call.enqueue(object : Callback<UtilizadorGET> {
            //retira da api a lista de utilizadores
            override fun onResponse(
                call: Call<UtilizadorGET>,
                response: Response<UtilizadorGET>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        //itera cada utilizador na lista de utilizadores
                        for (utilizadorIterado in it.listaUtilizadores) {
                            Log.d("USER", user)
                            Log.d("USER ITERADO", utilizadorIterado.userId.toString())
                            //se utilizador é o autenticado, remove o utilizador por chamada DELETE à API com o id do utilizador
                            if (user.equals(utilizadorIterado.userId.toString())) {
                                Log.d("USER ID", utilizadorIterado.id.toString())
                                // botão para remover a conta que foi selecionada
                                val call = RetrofitInitializer().servicoAPI()
                                    .removerUtilizador(utilizadorIterado.id.toString())
                                Log.d("USER ID", utilizadorIterado.id.toString())
                                call.enqueue(object : Callback<RespostaAPI> {
                                    override fun onResponse(
                                        call: Call<RespostaAPI>,
                                        response: Response<RespostaAPI>
                                    ) {
                                        if (response.isSuccessful) {
                                            listarObs(user)
                                        } else {
                                            Log.e(
                                                "Estado: ",
                                                "Falha ao remover conta de utilizador"
                                            )
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<RespostaAPI>,
                                        t: Throwable
                                    ) {
                                        Log.e(
                                            "ObservacaoListAdapter",
                                            "Erro ao remover conta de utilizador",
                                            t
                                        )
                                    }
                                })

                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UtilizadorGET>, t: Throwable) {
                Log.d("AboutActivity", "onFailure: ${t.message}")
            }
        })
    }

    /*
    * Dado o utilizador, vai buscar à API a lista de observações referentes ao utilizador
     */
    private fun listarObs(user: String) {
        val call = RetrofitInitializer().servicoAPI().listarObservacoes()
        call.enqueue(object : Callback<ObservacaoGET> {
            override fun onResponse(
                call: Call<ObservacaoGET>,
                response: Response<ObservacaoGET>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        // remover as observações a partir do maior índice, porque a API do Sheety
                        // reordena os índices após a remoção
                        listaObs = it.listaObservacoes
                        ajustarObsRemover(user)
                    }
                }
            }

            override fun onFailure(call: Call<ObservacaoGET>, t: Throwable) {
                Log.d("AboutActivity", "onFailure: ${t.message}")
            }
        })
    }

    /*
    * Reordena os índíces da lista de observações
     */
    private fun ajustarObsRemover(user:String){
        val novaListaObs = mutableListOf<Observacao>()

        for (obs in listaObs) {
            if (obs.utilizador.equals(user)) {
                novaListaObs.add(obs)
            }
        }
        listaObs = novaListaObs
        removerObs(user)
    }

    /*
    * Remove observação da lista da API
     */
    private fun removerObs(user: String) {
        for(i in listaObs.size-1 downTo 0){

                    val call = RetrofitInitializer().servicoAPI()
                        .removerObservacao(listaObs[i].id.toString())
                    call.enqueue(object : Callback<RespostaAPI> {
                        override fun onResponse(
                            call: Call<RespostaAPI>,
                            response: Response<RespostaAPI>
                        ) {
                            if (!response.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Ocorreu um erro a remover a sua conta.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<RespostaAPI>,
                            t: Throwable
                        ) {
                            Log.e(
                                "AboutActivity",
                                "Falha ao remover observações do utilizador",
                                t
                            )
                        }
                    })

            sleep(800)
            }
        // Limpar o login guardado na sharedPreferences
        val sharedPref = getSharedPreferences("utilizadorAutenticado", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        loading.dismiss()
        }
    }

