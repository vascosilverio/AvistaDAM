package com.example.avista.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 112)
        }


        val sharedPref = getSharedPreferences("utilizador", MODE_PRIVATE)
        val utilizador = sharedPref.getString("utilizador","")


        Handler(Looper.getMainLooper()).postDelayed({
            if(utilizador == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                var intent = Intent(this, MainActivity::class.java)
                // enviar para a atividade Main o utilizador autenticado
                intent.putExtra("utilizador", utilizador)
                startActivity(intent)
                finish()
            }
        }, 1000)
    }
}