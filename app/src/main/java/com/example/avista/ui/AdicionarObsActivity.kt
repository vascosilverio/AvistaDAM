package com.example.avista.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.ParseException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.avista.R
import com.example.avista.databinding.ActivityAdicionarObsBinding
import com.example.avista.model.Observacao
import com.example.avista.model.ObservacaoPOST
import com.example.avista.model.RespostaAPI
import com.example.avista.retrofit.EnvioFotografia
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.retrofit.service.EnvioFotografiaCallback
import com.example.avista.retrofit.service.ServicoAPI
import com.example.avista.ui.activity.MapActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import com.bumptech.glide.Glide
import com.example.avista.model.ObservacaoSharedModel
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale


class AdicionarObsActivity : BaseActivity() {

    private lateinit var binding: ActivityAdicionarObsBinding
    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()
    private lateinit var viewModel: ObservacaoSharedModel
    private var listaObservacoes = ArrayList<Observacao>()
    private val CAMERA_PERMISSION_CODE = 101
    private val LOCATION_PERMISSION_CODE = 102
    private val PICK_MARKER_CODE = 103
    private val PICK_IMAGE_REQUEST = 1
    lateinit var imgBitmap: Bitmap
    var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    val IMAGE_CAPTURE_CODE = 654
    var imgURL = ""
    var latitude = 0.0
    var longitude = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarObsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ObservacaoSharedModel::class.java)

        //receber o utilizador da atividade Main
        val utilizador = intent.getStringExtra("utilizador").toString()

        // atribuir a data atual ao campo "Data"
        val dataFormatada = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = dataFormatada.format(Date())
        binding.txtData.setText(currentDate)

        verificarPermissaoLocalizacao()

        binding.btnCamera.setOnClickListener{
            verificarPermissaoCamera()
        }

        binding.btnGaleria.setOnClickListener{
            abrirGaleria()
        }

        binding.btnMapa.setOnClickListener {
            listaObservacoes = intent.getSerializableExtra("listaObservacoes") as ArrayList<Observacao>
            var intent = Intent(this, MapActivity::class.java)
            // enviar para a atividade Mapa a latitude e longitude atuais
            intent.putExtra("latitude", latitude.toString())
            intent.putExtra("longitude", longitude.toString())
            intent.putExtra("listaObservacoes", listaObservacoes)
            intent.putExtra("utilizador", utilizador)
            intent.putExtra("option", "OPTION_2")
            startActivityForResult(intent, PICK_MARKER_CODE)
        }

        binding.btnAdicionarObs.setOnClickListener {

            var especie = binding.txtEspecie.text.toString()
            var data = binding.txtData.text.toString()
            var descricao = binding.txtDescricao.text.toString()
            var id = ""

            // verifica se a data está no formato dd/mm/aaaa
            if (validarData(data)) {
                // criar um dialog para mostrar o loading enquanto os dados são enviados para as APIs
                val loading = Dialog(this)
                loading.setContentView(R.layout.loading)
                loading.window?.setLayout(
                    (resources.displayMetrics.widthPixels).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                loading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading.setCancelable(false)

                // usar o Glide para mostrar o GIF com movimento
                val loadingImageView = loading.findViewById<ImageView>(R.id.loadingImageView)
                Glide.with(this)
                    .load(R.drawable.loading)
                    .into(loadingImageView)
                loading.show()


                // verificar se foi selecionada alguma fotografia
                if (!::imgBitmap.isInitialized) {
                    Toast.makeText(
                        applicationContext,
                        "Não foi selecionada nenhuma fotografia.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loading.dismiss()
                } else if (especie == "") {
                    Toast.makeText(
                        applicationContext,
                        "Não foi especificada a espécie.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loading.dismiss()
                } else if (data == "") {
                    Toast.makeText(
                        applicationContext,
                        "Não foi especificada a data.",
                        Toast.LENGTH_SHORT
                    ).show()
                    loading.dismiss()
                } else {
                    // enviar fotografia para o ImgBB e guardar o displayURL que vai ser retornado
                    EnvioFotografia.enviarFoto(imgBitmap, applicationContext, object :
                        EnvioFotografiaCallback {
                        override fun onSucess(url: String) {
                            imgURL = url
                            // adicionar observação - latitude e longitude com valores de teste enquanto não se estão a obter as coordenadas de GPS
                            adicionarObs(
                                id,
                                utilizador,
                                latitude,
                                longitude,
                                imgURL,
                                descricao,
                                data,
                                especie,
                                loading
                            )
                        }

                        override fun onError(mensagemErro: String) {
                            Log.e(
                                "AdicionarObsActivity",
                                "Erro ao obter o displayURL: $mensagemErro"
                            )
                            Toast.makeText(
                                applicationContext,
                                "Erro ao obter o displayURL",
                                Toast.LENGTH_SHORT
                            ).show()
                            loading.dismiss()
                        }
                    })
                }
            }
        }
    }

    // função para validar que a data inserida é válida
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validarData(data: String): Boolean {
        try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val localDate = LocalDate.parse(data, formatter)
            return true
        } catch (e: DateTimeParseException) {
            Toast.makeText(
                applicationContext,
                "Data inválida. O formato da data deve ser dd/MM/aaaa.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }

    private fun verificarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        } else {
            localizacaoAtual()
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

            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    localizacaoAtual()
                } else {
                    Toast.makeText(this, "É preciso dar permissão de acesso à localização para obter a latitude e longitude.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun localizacaoAtual() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val gps: String = LocationManager.GPS_PROVIDER

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val localizacao: Location? = locationManager.getLastKnownLocation(gps)

            if (localizacao != null) {
                latitude = localizacao.latitude
                longitude = localizacao.longitude
            } else {
                Log.e("AdicionarObsActivity", "Erro a obter a localização.")
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
        } else if(requestCode == PICK_MARKER_CODE && resultCode == RESULT_OK){
            latitude = data?.getStringExtra("latitudeAtualizada")!!.toDouble()
            longitude = data?.getStringExtra("longitudeAtualizada")!!.toDouble()
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

    private fun adicionarObs(id: String, utilizador: String, lat: Double, long: Double, foto: String, descricao: String, data: String, especie: String, loading: Dialog){
        // criar o objeto Observacao
        val novaObservacao = Observacao(id = id, utilizador = utilizador, lat = lat.toString(), long = long.toString(), foto = foto, descricao = descricao, data = data, especie = especie)

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
                loading.dismiss()
            }

            override fun onFailure(call: Call<RespostaAPI>, t: Throwable) {
                Log.e("AdicionarObsActivity", "Erro: ${t.message}")
                loading.dismiss()
            }
        })

    }

}