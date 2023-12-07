package com.example.avista.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.avista.databinding.ActivityAdicionarObsBinding
import com.example.avista.model.Observacao
import com.example.avista.model.ObservacaoPOST
import com.example.avista.model.RespostaAPI
import com.example.avista.retrofit.EnvioFotografia
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.retrofit.service.EnvioFotografiaCallback
import com.example.avista.retrofit.service.ServicoAPI
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date


class AdicionarObsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarObsBinding
    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()
    private val CAMERA_PERMISSION_CODE = 101
    private val PICK_IMAGE_REQUEST = 1
    lateinit var imgBitmap: Bitmap
    var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    val IMAGE_CAPTURE_CODE = 654
    var imgURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarObsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //receber o utilizador da atividade Main
        val utilizador = intent.getStringExtra("utilizador").toString()

        // atribuir a data atual ao campo "Data"
        val dataFormatada = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = dataFormatada.format(Date())
        binding.txtData.setText(currentDate)

        binding.btnCamera.setOnClickListener{
            verificarPermissaoCamera()
        }

        binding.btnGaleria.setOnClickListener{
            abrirGaleria()
        }

        binding.btnAdicionarObs.setOnClickListener{
            var especie = binding.txtEspecie.text.toString()
            var data = binding.txtData.text.toString()
            var descricao = binding.txtDescricao.text.toString()
            // verificar se foi selecionada alguma fotografia
            if(!::imgBitmap.isInitialized) {
                Toast.makeText(applicationContext, "Não foi selecionada nenhuma fotografia.", Toast.LENGTH_SHORT).show()
            } else if(especie == "") {
                Toast.makeText(applicationContext, "Não foi especificada a espécie.", Toast.LENGTH_SHORT).show()
            } else if(data == "") {
                Toast.makeText(applicationContext, "Não foi especificada a data.", Toast.LENGTH_SHORT).show()
            } else {
                // enviar fotografia para o ImgBB e guardar o displayURL que vai ser retornado
                EnvioFotografia.enviarFoto(imgBitmap, applicationContext, object :
                    EnvioFotografiaCallback {
                    override fun onSucess(url: String) {
                        imgURL = url
                        // adicionar observação - latitude e longitude com valores de teste enquanto não se estão a obter as coordenadas de GPS
                        adicionarObs(
                            utilizador,
                            "35.000000",
                            "25.000000",
                            imgURL,
                            descricao,
                            data,
                            especie
                        )
                    }

                    override fun onError(mensagemErro: String) {
                        Log.e("AdicionarObsActivity", "Erro ao obter o displayURL: $mensagemErro")
                        Toast.makeText(
                            applicationContext,
                            "Erro ao obter o displayURL",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    private fun abrirGaleria() {
        val galeria = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galeria, PICK_IMAGE_REQUEST)
    }

    private fun tirarFoto() {
        // com recurso ao artigo https://hamzaasif-mobileml.medium.com/android-capturing-images-from-camera-or-gallery-as-bitmaps-d3eb1d68aeb2
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nova fotografia")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Através da aplicação Avista")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    private fun verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            tirarFoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tirarFoto()
                } else {
                    Toast.makeText(this, "É preciso dar a permissão de acesso à câmera para poder tirar fotografias.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // se o requestCode for o de selecionar image, é porque veio da galeria
            val uriImagem = data?.data
            imgBitmap = converterParaBitmap(uriImagem!!)!!
            binding.viewImagem.setImageBitmap(imgBitmap)

        } else if(requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK){
            // se o requestCode for o de capturar uma imagem, é porque veio da câmera
            imgBitmap = converterParaBitmap(image_uri!!)!!
            binding.viewImagem.setImageBitmap(imgBitmap)
        }
    }
    private fun converterParaBitmap(uriImagem: Uri): Bitmap? {
        try {
            val inputStream = contentResolver.openInputStream(uriImagem)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    private fun adicionarObs(utilizador: String, lat: String, long: String, foto: String, descricao: String, data: String, especie: String){
        // criar o objeto Observacao
        val novaObservacao = Observacao(utilizador = utilizador, lat = lat, long = long, foto = foto, descricao = descricao, data = data, especie = especie)

        // encapsular dentro de um objecto Observacao para construir corretamente o JSON a enviar
        val postObservacao = ObservacaoPOST(observacao = novaObservacao)
        Log.d("AdicionarObsActivity", "JSON enviado: ${Gson().toJson(postObservacao)}")

        val call = servicoAPI.adicionarObservacao(postObservacao)
        call.enqueue(object : Callback<RespostaAPI> {
            override fun onResponse(call: Call<RespostaAPI>, response: Response<RespostaAPI>) {
                if (response.isSuccessful) {
                    Log.d("AdicionarObsActivity", "onSucessful: ${response.body()}")
                    Toast.makeText(
                        applicationContext,
                        "Observação adicionada com sucesso.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Log.e("AdicionarObsActivity", "Erro: ${response.code()}")
                    Toast.makeText(
                        applicationContext,
                        "Erro. Código: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RespostaAPI>, t: Throwable) {
                Log.e("AdicionarObsActivity", "Erro: ${t.message}")
            }
        })

    }

}