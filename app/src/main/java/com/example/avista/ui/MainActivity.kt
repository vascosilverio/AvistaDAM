package com.example.avista.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.avista.adapter.ObservacaoListAdapter
import com.example.avista.data.ObservacaoMock
import com.example.avista.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val mock = ObservacaoMock()
        binding.recyclerView.adapter = ObservacaoListAdapter(mock.listaObservacoes,
            ObservacaoListAdapter.OnClickListener{
            Toast.makeText(this,it.date, Toast.LENGTH_SHORT).show()
        })
    }
}