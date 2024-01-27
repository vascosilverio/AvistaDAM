package com.projetodam.avista.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projetodam.avista.R
import com.projetodam.avista.databinding.ActivitySignupBinding
import com.projetodam.avista.model.RespostaAPI
import com.projetodam.avista.model.Utilizador
import com.projetodam.avista.model.UtilizadorPOST
import com.projetodam.avista.retrofit.RetrofitInitializer
import com.projetodam.avista.retrofit.service.ServicoAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import at.favre.lib.crypto.bcrypt.BCrypt
import com.projetodam.avista.model.UtilizadorGET


/*
* Classe que permite efetuar o registo de novos utilizadores
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ao clicar no botão registar, valida se os dados de acesso são válidos e envia os dados do novo utilizador para a API Sheety
        binding.buttonRegistar.setOnClickListener {
            val utilizador = binding.editarUsername.text.toString()
            val palavraPasse = binding.editarPassword.text.toString()
            val palavraPasseC = binding.confirmarPassword.text.toString()

            // verifica se todos os campos são válidos antes de proceder ao registo do utilizador
            if (utilizador.isNotEmpty() && palavraPasse.isNotEmpty() && palavraPasseC.isNotEmpty()) {
                if (palavraPasse == palavraPasseC) {
                    verificarUtilizador(utilizador, palavraPasse)
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.confirmar_password_falha),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.preenchimento_falha),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /*
    * verifica se a password cumpre os requisitos mínimos: 8 caracteres, pelo menos uma letra maiúscula,
    * pelo menos uma letra minúscula, pelo menos um número e pelo menos um caracter especial
     */
    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

    /*
    * se forem cumpridos todos os requisitos, adiciona o novo utilizador na API
     */
    private fun adicionaUtilizador(utilizador: String, palavraPasse: String) {
        // verifica se o email introduzido corresponde ao padrão de uma conta de email
        if(utilizador.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(utilizador).matches()){
        Toast.makeText(
            applicationContext,
            "Email Inválido.",
            Toast.LENGTH_SHORT
        ).show()}
        // verifica se a password cumpre os requisitos de segurança
        else if(!isValidPassword(palavraPasse)){
            Toast.makeText(
                applicationContext,
                "A palavra passe deve ter pelo menos 8 caracteres, um dígito, um caráter maiúsculo, um minúsculo e um caráter especial.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            // criar o objeto utilizador
            val novoUtilizador =
                Utilizador(id = "", userId = utilizador, password = encriptarPalavraPasse(palavraPasse))

            // encapsular dentro de um objecto Utilizador para construir corretamente o JSON a enviar
            val postUtilizador = UtilizadorPOST(utilizador = novoUtilizador)
            val call = servicoAPI.adicionarUtilizador(postUtilizador)
            // enviar o utilizador para a API
            call.enqueue(object : Callback<RespostaAPI> {
                override fun onResponse(call: Call<RespostaAPI>, response: Response<RespostaAPI>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Registado com sucesso.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Log.e("LoginActivity", "Erro: ${response.code()}")
                        Toast.makeText(
                            applicationContext,
                            "Erro. Código: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RespostaAPI>, t: Throwable) {
                    Log.e("LoginActivity", "Erro: ${t.message}")
                }
            })
        }
    }

    /*
    * verifica se o nome de utilizador (email) já existe registado
     */
    private fun verificarUtilizador(utilizador: String, palavraPasse: String) {
        val call = servicoAPI.listarUtilizadores()

        // ontem a lista de utilizadores da API
        call.enqueue(object : Callback<UtilizadorGET> {
            override fun onResponse(call: Call<UtilizadorGET>, response: Response<UtilizadorGET>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        // para cada utilizador obtido da API, verifica se a conta de email coincide com a que está a tentar registar
                        for (utilizadorIterado in it.listaUtilizadores) {
                            if (utilizadorIterado.userId == utilizador) {
                                Toast.makeText(
                                    applicationContext,
                                    "O nome de utilizador ${utilizador} já existe.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                        }
                        // se a conta de email for nova, adicionar o utilizador
                        adicionaUtilizador(utilizador, palavraPasse)
                    }
                }
            }
            override fun onFailure(call: Call<UtilizadorGET>, t: Throwable) {
                Log.d("LoginActivity", "Erro ao verificar se o utilizador já existe registado.")
            }
        })
    }

    /*
    * encriptar a palavra-passe com o BCrypt
     */
    private fun encriptarPalavraPasse(palavraPasse: String): String {
        val bcrypt = BCrypt.withDefaults()
        return bcrypt.hashToString(10, palavraPasse.toCharArray())
    }
}