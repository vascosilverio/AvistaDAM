package com.example.avista.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.avista.R
import com.example.avista.model.APIResult
import com.example.avista.model.Utilizador
import com.example.avista.model.Utilizador2
import com.example.avista.model.UtilizadorDados
import com.example.avista.retrofit.RetroFitInitializer
import com.example.avista.retrofit.RetrofitInitializer2
import com.example.avista.ui.activity.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_screen) // Replace with the actual layout name

        val registerButton: Button = findViewById(R.id.registerButton)
        //val user = Utilizador2(R.id.editTextFullName.toString(), R.id.editTextPassword.toString() )

        registerButton.setOnClickListener {
            val fullName = findViewById<EditText>(R.id.editTextFullName).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val user = Utilizador2(fullName, password)
            registarUtilizador(user){
                //Toast.makeText(this,"Add " + it?.description,Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun registarUtilizador(user: Utilizador2, onResult: (APIResult?) -> Unit){
        val call = RetrofitInitializer2().userService().addUser(user.userName, user.password)
        Toast.makeText(this,"Username = " + user.userName + " Password = " + user.password,Toast.LENGTH_SHORT).show()
        call.enqueue(
            object : Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable) {
                    t.printStackTrace()
                    onResult(null)
                }
                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>) {
                    val addedNote = response.body()
                    onResult(addedNote)
                }
            }
        )
    }

}
