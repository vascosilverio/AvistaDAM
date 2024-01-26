package com.projetodam.avista.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.projetodam.avista.R
import com.projetodam.avista.databinding.FullscreenObservacaoBinding
import com.squareup.picasso.Picasso

class FullscreenObservacaoActivity : AppCompatActivity() {

    private lateinit var binding: FullscreenObservacaoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FullscreenObservacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the URI from the intent
        val imageUri = intent.getStringExtra("imageUri")
        val img: ImageView = this.findViewById<ImageView>(R.id.imgDisplay)
        // Display the image in the ImageView
        Picasso.get().load(imageUri!!.toUri()).into(img)
    }
}