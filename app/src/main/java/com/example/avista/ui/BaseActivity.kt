package com.example.avista.ui

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.ui.activity.MapActivity


open class BaseActivity : AppCompatActivity() {

    private lateinit var utilizador: String
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.bottom_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_mapa -> {
                var intent = Intent(this@BaseActivity, MapActivity::class.java)
                // enviar para a atividade Main o utilizador autenticado

                startActivity(intent)
                true
            }
            R.id.item_observacoes -> {
                //receber o utilizador da atividade de Login
                utilizador = intent.getStringExtra("utilizador").toString()
                var intent = Intent(this@BaseActivity, MainActivity::class.java)
                // enviar para a atividade Main o utilizador autenticado
                intent.putExtra("utilizador", utilizador)
                startActivity(intent)
                true
            }
            R.id.item_Logout ->
                {
                // Limpar o login guardado na sharedPreferences
                val sharedPref = getSharedPreferences("utilizadorAutenticado", MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.item_Info -> {
                utilizador = intent.getStringExtra("utilizador").toString()
                var intent = Intent(this@BaseActivity, AboutActivity::class.java)
                // enviar para a atividade Main o utilizador autenticado
                intent.putExtra("utilizador", utilizador)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}