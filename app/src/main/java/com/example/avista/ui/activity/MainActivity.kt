package com.example.avista.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.model.Utilizador
import com.example.avista.model.UtilizadorDados
import com.example.avista.retrofit.RetroFitAutenticacao
import com.example.avista.retrofit.RetroFitInitializer
import com.example.avista.ui.activity.BirdRegistrationActivity
import com.example.avista.ui.activity.LoginActivity
import com.example.avista.ui.activity.MapActivity
import com.example.avista.ui.activity.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.birdRegistrationButton).setOnClickListener {
            val intent = Intent(this, BirdRegistrationActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.mapButton).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }


}