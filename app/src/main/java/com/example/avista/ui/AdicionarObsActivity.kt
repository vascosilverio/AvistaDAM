package com.example.avista.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.databinding.ActivityAdicionarObsBinding
import com.example.avista.retrofit.EnvioFotografia
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.FileNotFoundException


class AdicionarObsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarObsBinding
    private val PICK_IMAGE_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarObsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener{
            val img = "drawable/bird_logo.jpg"
            //var bitmap = convertImageToBitmap(img)
            //val testE = testExecute.testExecute(bitmap, applicationContext)
        }

        binding.btnEscolherFotoGaleria.setOnClickListener{
            abrirGaleria()
        }

        binding.btnAdicionarObs.setOnClickListener{
            var img = binding.viewImagem
            uploadFoto()
        }


    }


    private fun abrirGaleria() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                val bitmap = convertImageToBitmap(selectedImageUri)
                if (bitmap != null) {
                    binding.viewImagem.setImageBitmap(bitmap)
                    EnvioFotografia.testExecute(bitmap, applicationContext)
                }
            }
        }
    }
    private fun convertImageToBitmap(imageUri: Uri): Bitmap? {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    private fun uploadFoto(){


    }


}