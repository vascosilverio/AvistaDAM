package com.example.avista.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.model.APIResult
import com.example.avista.model.Utilizador
import com.example.avista.model.UtilizadorRequest
import com.example.avista.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_screen)

        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val fullName = findViewById<EditText>(R.id.editTextFullName).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val user = Utilizador(fullName, password)

            val userRequest = UtilizadorRequest(user)

            Log.d("RegisterActivity", "User: $user")

            registarUtilizador(userRequest) { apiResult ->
                Log.d("RegisterActivity", "Resposta da API: $apiResult")

            }
        }
    }

    public fun registarUtilizador(userRequest: UtilizadorRequest, onResult: (APIResult?) -> Unit) {
        val call = RetrofitInitializer().userService().addUser(userRequest)
        Toast.makeText(this, "Username = ${userRequest.utilizador.userId} Password = ${userRequest.utilizador.password}", Toast.LENGTH_SHORT).show()

        call.enqueue(object : Callback<APIResult> {
            override fun onFailure(call: Call<APIResult>, t: Throwable) {
                val response = (t as? HttpException)?.response()
                if (response != null && response.code() == 400) {
                    val errorBody = response.errorBody()?.string()
                    Log.d("RegisterActivity", "Erro 400. Corpo da resposta: $errorBody")
                } else {
                    Log.d("RegisterActivity", "Erro na requisição", t)
                }

                findViewById<TextView>(R.id.txtRegisterError).text = "ERRO"
                onResult(null)
            }

            override fun onResponse(call: Call<APIResult>, response: Response<APIResult>) {
                Log.d("RegisterActivity", "Código de resposta: ${response.code()}")

                if (response.isSuccessful) {
                    Log.d("RegisterActivity", "Corpo da resposta: ${response.body()}")
                    val addedNote = response.body()
                    onResult(addedNote)
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d("RegisterActivity", "Erro no código de resposta ${response.code()}. Corpo da resposta: $errorBody")
                    } catch (e: Exception) {
                        Log.e("RegisterActivity", "Erro ao obter o corpo da resposta de erro", e)
                    }

                    findViewById<TextView>(R.id.txtRegisterError).text = "ERRO"
                    onResult(null)
                }
            }
        })
    }
}
