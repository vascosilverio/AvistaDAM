package com.example.avista.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.avista.R
import com.example.avista.ui.activity.MapActivity

class BirdRegistrationActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var imageViewBird: ImageView
    private lateinit var editTextDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.birdreg_screen)

        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val speciesName = findViewById<EditText>(R.id.editTextSpeciesName).text.toString()
            val description = editTextDescription.text.toString()

            if (speciesName.isNotEmpty() && description.isNotEmpty()) {
                Toast.makeText(this, "Bird registration successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageViewBird.setImageBitmap(imageBitmap)
        }
    }
}