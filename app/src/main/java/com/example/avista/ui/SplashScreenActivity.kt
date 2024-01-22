package com.example.avista.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.avista.R
import com.example.avista.model.UtilizadorGET
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.retrofit.service.ServicoAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenActivity : AppCompatActivity() {

    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Verificar se existem permissões para aceder à memória
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 112)
        }

        // obter os dados de acesso da SharedPreferences
        val sharedPref = getSharedPreferences("utilizadorAutenticado", MODE_PRIVATE)
        val utilizador = sharedPref.getString("utilizador","")
        val password = sharedPref.getString("pwEncriptada","")


        // se existirem dados de autenticação na shared preferences, tentar autenticar com esses dados (a password vem encriptada)
        Handler(Looper.getMainLooper()).postDelayed({
            if(utilizador == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                verificarUtilizador(utilizador, password)
            }
        }, 1000)
    }

    private fun verificarUtilizador(utilizador: String?, palavraPasse: String?) {
        val call = servicoAPI.listarUtilizadores()

        call.enqueue(object : Callback<UtilizadorGET> {
            override fun onResponse(call: Call<UtilizadorGET>, response: Response<UtilizadorGET>) {
                Log.d("Código da resposta da API:", response.code().toString())

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val listaUtilizadores = it.listaUtilizadores

                        if (listaUtilizadores != null && listaUtilizadores.isNotEmpty()) {
                            for (utilizadorIterado in listaUtilizadores) {
                                Log.d("Utilizador API:", utilizadorIterado.userId!!)
                                Log.d("Password API:", utilizadorIterado.password!!)

                                // se o utilizador e a password estiverem corretos, autenticar e iniciar a MainActivity
                                if (utilizadorIterado.userId!! == utilizador!! && palavraPasse!! == utilizadorIterado.password!!) {
                                    Log.d("AUTENTICACAO COM SUCESSO!", "AUTENTICADO")
                                    var intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                                    // enviar para a atividade Main o utilizador autenticado
                                    intent.putExtra("utilizador", utilizador)
                                    startActivity(intent)
                                    finish()
                                    return
                                }
                            }
                        }
                    }
                }
                Log.d("AUTENTICACAO FALHOU!", "Não autenticado")
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<UtilizadorGET>, t: Throwable) {
                Log.d("LoginActivity", "onFailure: ${t.message}")
            }
        })
    }

}