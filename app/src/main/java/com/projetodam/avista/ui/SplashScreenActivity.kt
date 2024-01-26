package com.projetodam.avista.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.projetodam.avista.R
import com.projetodam.avista.model.UtilizadorGET
import com.projetodam.avista.retrofit.RetrofitInitializer
import com.projetodam.avista.retrofit.service.ServicoAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
* Classe inicial onde mostra o logotipo da aplicação e tenta realizar
* O login com os dados armazenados na sharedPreferences
*/
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

    /*
    * Verifica se o utilizador guardado na sharedPreferences e a hash da password permitem autenticação automática
    */
    private fun verificarUtilizador(utilizador: String?, palavraPasse: String?) {
        val call = servicoAPI.listarUtilizadores()

        call.enqueue(object : Callback<UtilizadorGET> {
            override fun onResponse(call: Call<UtilizadorGET>, response: Response<UtilizadorGET>) {
                Log.d("Código da resposta da API:", response.code().toString())

                // se a resposta da API a obter a lista de utilizadores for retornada com sucesso
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val listaUtilizadores = it.listaUtilizadores
                        if (listaUtilizadores != null && listaUtilizadores.isNotEmpty()) {
                            // itera cada utilizador retornado pela API para comparar com a hash da password
                            for (utilizadorIterado in listaUtilizadores) {
                                // se o utilizador e a password estiverem corretos, autenticar e iniciar a MainActivity
                                if (utilizadorIterado.userId!! == utilizador!! && palavraPasse!! == utilizadorIterado.password!!) {
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
                // se os dados de acesso guardados na sharedPreferences não forem válidos, vai solicitar os dados de acesso
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<UtilizadorGET>, t: Throwable) {
                Log.d("LoginActivity", "onFailure: ${t.message}")
            }
        })
    }

}