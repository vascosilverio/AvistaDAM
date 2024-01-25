package com.example.avista.ui

import android.os.Bundle
import com.example.avista.databinding.AboutBinding

class AboutActivity : BaseActivity() {

    private lateinit var binding: AboutBinding
    private lateinit var utilizador: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}