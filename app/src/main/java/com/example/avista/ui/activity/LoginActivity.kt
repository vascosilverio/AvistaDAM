package com.example.avista.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.avista.R
import com.example.avista.model.Utilizador
import com.example.avista.model.Utilizador2
import com.example.avista.model.UtilizadorDados
import com.example.avista.retrofit.RetroFitAutenticacao
import com.example.avista.retrofit.RetroFitInitializer
import com.example.avista.retrofit.RetrofitInitializer2
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen) // Replace with the actual layout name

        val loginButton2: Button = findViewById(R.id.loginButton2)

        loginButton2.setOnClickListener {
            // Retrieve username/email and password from EditText fields
            val username = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            verificarAutorizacao(R.id.editTextUsername.toString(), R.id.editTextPassword.toString())

            // Toast.makeText(this, "Users: " + user, Toast.LENGTH_SHORT).show()
        }


    }

    public fun verificarAutorizacao(userName: String, password: String){
        val call = RetrofitInitializer2().userService().list("coiso")
        call.enqueue(object : Callback<List<Utilizador2>?> {
            override fun onResponse(call: Call<List<Utilizador2>?>?,
                                    response: Response<List<Utilizador2>?>?) {
                response?.body()?.let {
                    val users: List<Utilizador2> = it
                    checkUser(users)
                }
            }

            override fun onFailure(call: Call<List<Utilizador2>?>?, t: Throwable?) {
                t?.message?.let { Log.e("onFailure error", it) }
            }
        })
        findViewById<TextView>(R.id.printTest).text = call.request().toString()
    }


    private fun checkUser(users: List<Utilizador2>) {
        findViewById<TextView>(R.id.printTest2).text = users.toString()
    }
}