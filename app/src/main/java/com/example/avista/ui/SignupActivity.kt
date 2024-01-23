package com.example.avista.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.databinding.ActivitySignupBinding
import com.example.avista.model.RespostaAPI
import com.example.avista.model.Utilizador
import com.example.avista.model.UtilizadorPOST
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.retrofit.service.ServicoAPI
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import at.favre.lib.crypto.bcrypt.BCrypt

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegistar.setOnClickListener {
            val utilizador = binding.editarUsername.text.toString()
            val palavraPasse = binding.editarPassword.text.toString()
            val palavraPasseC = binding.confirmarPassword.text.toString()

            if (utilizador.isNotEmpty() && palavraPasse.isNotEmpty() && palavraPasseC.isNotEmpty()) {
                if (palavraPasse == palavraPasseC) {
                    //validar signup
                    //caso falhe limpar os campos de preenchimento
                    adicionaUtilizador(utilizador, palavraPasse)
                    finish()
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

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

    private fun adicionaUtilizador(utilizador: String, palavraPasse: String) {
        if(utilizador.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(utilizador).matches()){
        Toast.makeText(
            applicationContext,
            "Email Inválido.",
            Toast.LENGTH_SHORT
        ).show()}
        else if(!isValidPassword(palavraPasse)){
            Toast.makeText(
                applicationContext,
                "A palavra passe deve ter pelo menos 8 caracteres, um dígito, um caráter maiúsculo, um minúsculo e um caráter especial.",
                Toast.LENGTH_LONG
            ).show()
        }else {
            // criar o objeto utilizador
            val novoUtilizador =
                Utilizador(userId = utilizador, password = encriptarPalavraPasse(palavraPasse))

            // encapsular dentro de um objecto Utilizador para construir corretamente o JSON a enviar
            val postUtilizador = UtilizadorPOST(utilizador = novoUtilizador)
            Log.d("LoginActivity", "JSON enviado: ${Gson().toJson(postUtilizador)}")

            val call = servicoAPI.adicionarUtilizador(postUtilizador)
            call.enqueue(object : Callback<RespostaAPI> {
                override fun onResponse(call: Call<RespostaAPI>, response: Response<RespostaAPI>) {
                    if (response.isSuccessful) {
                        Log.d("LoginActivity", "onSucessful: ${response.body()}")
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

    // encriptar a palavra-passe com o BCrypt
    private fun encriptarPalavraPasse(palavraPasse: String): String {
        val bcrypt = BCrypt.withDefaults()
        return bcrypt.hashToString(10, palavraPasse.toCharArray())
    }
}