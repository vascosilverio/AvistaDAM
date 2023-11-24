package com.example.avista.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            val utilizador = binding.editUsername.text.toString()
            val palavraPasse = binding.editarPassword.text.toString()

            if(utilizador.isNotEmpty() && palavraPasse.isNotEmpty()){
                //if(validar login com sucesso){

                startActivity(Intent(this,MainActivity::class.java))
                //finish()
                //    }else{
                //      Toast.makeText(
                //                    applicationContext,
                //                    getString(R.string.login_falha),
                //                        Toast.LENGTH_SHORT
                //                ).show()
                //binding.editarUsername.setText("")
                //binding.editarPpassword.setText("")
                //}
            }else{
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
}