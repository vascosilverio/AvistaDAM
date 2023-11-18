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
import com.example.avista.model.UtilizadorResponse
import com.example.avista.retrofit.RetroFitAutenticacao
import com.example.avista.retrofit.RetroFitInitializer
import com.example.avista.retrofit.RetrofitInitializer2
import com.example.avista.retrofit.service.UserService
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

const val BASE_URL = "https://api.sheety.co/6c966be3aa95146fdfd60b287a41e909/aveDam/"
var userAuth = ""

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen) // Replace with the actual layout name

        val loginButton2: Button = findViewById(R.id.loginButton2)

        loginButton2.setOnClickListener {
            userAuth = ""
            // Retrieve username/email and password from EditText fields
            val username = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            // Toast.makeText(this, "Users: " + user, Toast.LENGTH_SHORT).show()
            getUsers(username, password);
        }

    }
     private fun getUsers(username: String, password: String){
         val retrofitBuilder = Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl(BASE_URL)
             .build()
             .create(UserService::class.java)

         val retrofitData = retrofitBuilder.list("Bearer coiso")

         retrofitData.enqueue(object : Callback<UtilizadorResponse> {
             override fun onResponse(call: Call<UtilizadorResponse>, response: Response<UtilizadorResponse>) {
                 if (response.isSuccessful) {
                     val responseBody = response.body()

                     responseBody?.let {
                         val myStringBuilder = StringBuilder()
                         for (dados in it.utilizadorList) {
                             if(username == dados.userId){
                                 if(password == dados.password) {
                                     userAuth = username
                                 }
                             }
                             myStringBuilder.append(dados.userId)
                             //myStringBuilder.append("\n")
                         }
                         if(userAuth == ""){
                             findViewById<TextView>(R.id.printTest).text = "Verifique o nome de utilizador ou a password."
                         } else {
                            findViewById<TextView>(R.id.printTest).text = "User autenticado: " + userAuth
                         }
                     }
                 } else {
                     Log.d("LoginActivity", "onResponse: ${response.code()}")
                     findViewById<TextView>(R.id.printTest2).text = "teste erro"
                 }
             }

             override fun onFailure(call: Call<UtilizadorResponse>, t: Throwable) {
                 Log.d("LoginActivity", "onFailure: ${t.message}")
                 findViewById<TextView>(R.id.printTest2).text = "teste erro"
             }
         })
     }

}