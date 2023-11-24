package com.example.avista.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

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
}