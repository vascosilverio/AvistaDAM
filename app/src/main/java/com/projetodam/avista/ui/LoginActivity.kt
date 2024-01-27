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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.textoRegistar.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.textoRecuperar.setOnClickListener {
            //recuperar password
        }

    }
    private fun verificarUtilizador(utilizador: String, palavraPasse: String) {
        val call = servicoAPI.listarUtilizadores()

        Log.d("LoginActivity", "utilizador: ${utilizador}")
        call.enqueue(object : Callback<UtilizadorGET> {
            override fun onResponse(call: Call<UtilizadorGET>, response: Response<UtilizadorGET>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {

                        for (utilizadorIterado in it.listaUtilizadores) {
                            Log.d("LoginActivity", "utilizador na lista: ${utilizadorIterado.userId} - ${utilizador}")
                            Log.d("LoginActivity", "utilizador na lista: ${utilizadorIterado.password} - ${palavraPasse}")

                            if (utilizadorIterado.userId == utilizador && verificarPalavraPasse(palavraPasse, utilizadorIterado.password)) {
                                if(binding.checkboxLogged.isChecked) {
                                    val sharedPref = getSharedPreferences("utilizadorAutenticado", MODE_PRIVATE)
                                    val editor = sharedPref.edit()
                                    editor.putString("utilizador", utilizadorIterado.userId)
                                    editor.putString("pwEncriptada", utilizadorIterado.password)
                                    editor.apply()
                                }
                                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                                // enviar para a atividade Main o utilizador autenticado
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

    // usar o BCrypt para confirmar que a hash é a mesma da base de dados
    private fun verificarPalavraPasse(palavraPasse: String, hashPalavraPasse: String?): Boolean {
        val bcrypt = BCrypt.verifyer()
        return bcrypt.verify(palavraPasse.toCharArray(), hashPalavraPasse).verified
    }
}