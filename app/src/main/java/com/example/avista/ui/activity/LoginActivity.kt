package com.example.avista.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen) // Replace with the actual layout name

        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            // Retrieve username/email and password from EditText fields
            val username = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            // Here you would implement authentication logic
            // For example, using Sheety, you might make an API call to check credentials
            // This is a basic example for illustration purposes only
            if (username == "yourUsername" && password == "yourPassword") {
                // Authentication successful, navigate to the next screen
                val intent = Intent(this, BirdRegistrationActivity::class.java)
                startActivity(intent)
            } else {
                // Authentication failed, show an error (you'd handle errors more gracefully)
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}