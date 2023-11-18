package com.example.avista.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.model.UtilizadorResponse
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.retrofit.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val BASE_URL = "https://api.sheety.co/6c966be3aa95146fdfd60b287a41e909/aveDam/"
var userAuth = ""

class LoginActivity : AppCompatActivity() {

    private val userService: UserService = RetrofitInitializer().userService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        val loginButton2: Button = findViewById(R.id.loginButton2)

        loginButton2.setOnClickListener {
            userAuth = ""
            val username = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            getUsers(username, password)
        }
    }

    private fun getUsers(username: String, password: String) {
        val call = userService.list()

        call.enqueue(object : Callback<UtilizadorResponse> {
            override fun onResponse(call: Call<UtilizadorResponse>, response: Response<UtilizadorResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    responseBody?.let {
                        for (dados in it.utilizadorList) {
                            if (username == dados.userId && password == dados.password) {
                                userAuth = username
                            }
                        }

                        if (userAuth.isEmpty()) {
                            findViewById<TextView>(R.id.printTest).text = "Verifique o nome de utilizador ou a password."
                        } else {
                            findViewById<TextView>(R.id.printTest).text = "User autenticado: $userAuth"
                        }
                    }
                } else {
                    Log.d("LoginActivity", "onResponse: ${response.code()}")
                    findViewById<TextView>(R.id.printTest2).text = "Erro na resposta da API"
                }
            }

            override fun onFailure(call: Call<UtilizadorResponse>, t: Throwable) {
                Log.d("LoginActivity", "onFailure: ${t.message}")
                findViewById<TextView>(R.id.printTest2).text = "Erro na chamada da API"
            }
        })
    }
}