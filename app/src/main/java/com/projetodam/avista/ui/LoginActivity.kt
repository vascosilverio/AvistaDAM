package com.projetodam.avista.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.favre.lib.crypto.bcrypt.BCrypt
import com.projetodam.avista.R
import com.projetodam.avista.databinding.ActivityLoginBinding
import com.projetodam.avista.model.UtilizadorGET
import com.projetodam.avista.retrofit.RetrofitInitializer
import com.projetodam.avista.retrofit.service.ServicoAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
*   Classe que permite que o utilizador se autentique
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // listener que recebe o username e a password do utilizador,
        // verifica se os campos não estão vazios, e posteriormente
        // chama a função que irá validar com a API se os dados são válidos
        binding.buttonLogin.setOnClickListener {
            val utilizador = binding.editUsername.text.toString()
            val palavraPasse = binding.editarPassword.text.toString()
            if(utilizador != "" && palavraPasse != "") {
                verificarUtilizador(utilizador, palavraPasse)
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.preenchimento_falha),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Se o utilizador não tiver conta, pode clicar nesta opção para
        // ser redirecionado para a página de registo
        binding.textoRegistar.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    /*
    *   Função que obtém os utilizadores da API, verifica qual deles coincide com o que está a se autenticar,
    *   encripta novamente a password utilizada e verifica se coincide com a hash armazenada na API
     */
    private fun verificarUtilizador(utilizador: String, palavraPasse: String) {
        // obter todos os utilizadores da API
        val call = servicoAPI.listarUtilizadores()
        call.enqueue(object : Callback<UtilizadorGET> {
            override fun onResponse(call: Call<UtilizadorGET>, response: Response<UtilizadorGET>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        // verifica qual dos utilizadores é o que se está a autenticar, e verifica se a hash gerada da password coincide com a da API
                        for (utilizadorIterado in it.listaUtilizadores) {
                            if (utilizadorIterado.userId == utilizador && verificarPalavraPasse(palavraPasse, utilizadorIterado.password)) {
                                // se o utilizador clicar na opção para manter sessão iniciada, é usada a sharedPreferences para armazenar os dados
                                if(binding.checkboxLogged.isChecked) {
                                    val sharedPref = getSharedPreferences("utilizadorAutenticado", MODE_PRIVATE)
                                    val editor = sharedPref.edit()
                                    editor.putString("utilizador", utilizadorIterado.userId)
                                    editor.putString("pwEncriptada", utilizadorIterado.password)
                                    editor.apply()
                                }
                                // enviar para a atividade Main o utilizador autenticado
                                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("utilizador", utilizador)
                                startActivity(intent)
                                return
                            }
                        }
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.verificar_password),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UtilizadorGET>, t: Throwable) {
                Log.d("LoginActivity", "onFailure: ${t.message}")
            }
        })
    }

    /*
    * usar o BCrypt para confirmar que a hash é a mesma da base de dados
    */
    private fun verificarPalavraPasse(palavraPasse: String, hashPalavraPasse: String?): Boolean {
        val bcrypt = BCrypt.verifyer()
        return bcrypt.verify(palavraPasse.toCharArray(), hashPalavraPasse).verified
    }
}