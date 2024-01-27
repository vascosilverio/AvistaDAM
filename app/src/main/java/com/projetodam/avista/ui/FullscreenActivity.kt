package com.projetodam.avista.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.projetodam.avista.R
import com.projetodam.avista.databinding.FullscreenObservacaoBinding
import com.squareup.picasso.Picasso

/*
* Classe Activity que apresenta a imagem selecionada no detalhe da lista de observações na tela inteira
 */
class FullscreenObservacaoActivity : AppCompatActivity() {

    private lateinit var binding: FullscreenObservacaoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FullscreenObservacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // atualiza a image do intent
        val imageUri = intent.getStringExtra("imageUri")
        val img: ImageView = this.findViewById<ImageView>(R.id.imgDisplay)
        // introduz a imagem na view
        Picasso.get().load(imageUri!!.toUri()).into(img)
    }
}