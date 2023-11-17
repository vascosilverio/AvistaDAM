package com.example.avista.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.model.Utilizador
import com.example.avista.model.UtilizadorDados
import com.example.avista.retrofit.RetroFitInitializer
import com.example.avista.ui.activity.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_screen) // Replace with the actual layout name

        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            // Retrieve registration details from EditText fields
            val fullName = findViewById<EditText>(R.id.editTextFullName).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            // Here you would implement the registration logic
            // For example, using Sheety to save user details
            // This is a basic example for illustration purposes only
            if (fullName.isNotEmpty() && password.isNotEmpty()) {
                // Registration successful (in a real scenario, this would involve API calls to save data)
                val bytes = password.encodeToByteArray()

                main(fullName,password)

                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                // Assuming successful registration, you might navigate back to the login screen or another relevant activity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // If registration fails due to empty fields, show an error (you'd handle errors more gracefully)
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun main(name:String, password:String) {
        // Configurar o Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.sheety.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Criar uma instância da interface SheetyApiService
        val sheetyApiService = retrofit.create(RetroFitInitializer::class.java)

        // Criar um objeto Utilizador com os dados desejados
        val utilizador = Utilizador(UtilizadorDados(name, password))

        // Usar Coroutines para realizar a chamada POST em uma thread separada
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = sheetyApiService.criarUtilizador(utilizador).execute()

                // Verificar a resposta na thread principal usando launch(Dispatchers.Main)
                launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        println("Utilizador criado com sucesso!")
                    } else {
                        println("Erro ao criar utilizador. Código de resposta: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                println("Erro na solicitação: ${e.message}")
            }
        }

        // Aguardar para manter o programa em execução enquanto as corrotinas estão trabalhando
        Thread.sleep(5000)
    }

}
