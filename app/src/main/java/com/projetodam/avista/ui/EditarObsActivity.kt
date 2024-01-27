package com.projetodam.avista.ui

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.projetodam.avista.R
import com.projetodam.avista.databinding.ActivityEditarObsBinding
import com.projetodam.avista.model.Observacao
import com.projetodam.avista.model.ObservacaoPUT
import com.projetodam.avista.model.ObservacaoSharedModel
import com.projetodam.avista.model.RespostaAPI
import com.projetodam.avista.retrofit.EnvioFotografia
import com.projetodam.avista.retrofit.RetrofitInitializer
import com.projetodam.avista.retrofit.service.EnvioFotografiaCallback
import com.projetodam.avista.retrofit.service.ServicoAPI
import com.projetodam.avista.ui.activity.MapActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/*
* Classe Activity de Edição da Observação selecionada na RecyclerView de observações com verificação de campos e atualização
* na API Sheety dos campos alterados
 */
class EditarObsActivity : BaseActivity() {

    private lateinit var binding: ActivityEditarObsBinding
    val servicoAPI: ServicoAPI = RetrofitInitializer().servicoAPI()
    private lateinit var viewModel: ObservacaoSharedModel
    private var listaObservacoes = ArrayList<Observacao>()
    private val CAMERA_PERMISSION_CODE = 101
    private val PICK_MARKER_CODE = 103
    private val PICK_IMAGE_REQUEST = 1
    lateinit var imgBitmap: Bitmap
    var image_uri: Uri? = null
    val IMAGE_CAPTURE_CODE = 654
    var idObs = ""
    var utilizador = ""
    var data = ""
    var especie = ""
    var foto = ""
    var descricao = ""
    var latitude = 0.0
    var longitude = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarObsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ObservacaoSharedModel::class.java)

        //recebe os dados a editar
        idObs = intent.getStringExtra("idObs").toString()
        utilizador = intent.getStringExtra("utilizador").toString()
        utilizador = intent.getStringExtra("utilizador").toString()
        data = intent.getStringExtra("data").toString()
        especie = intent.getStringExtra("especie").toString()
        foto = intent.getStringExtra("foto").toString()
        descricao = intent.getStringExtra("descricao").toString()
        latitude = intent.getStringExtra("latitude")!!.toDouble()
        longitude = intent.getStringExtra("longitude")!!.toDouble()
        binding.txtData.setText(data)
        binding.txtEspecie.setText(especie)
        binding.txtDescricao.setText(descricao)
        Picasso.get().load(foto).into(binding.viewImagem)


        binding.btnCamera.setOnClickListener{
            verificarPermissaoCamera()
        }

        binding.btnGaleria.setOnClickListener{
            abrirGaleria()
        }

        //abre o mapa na localizaçao do utilizador
        binding.btnMapa.setOnClickListener {
            listaObservacoes = intent.getSerializableExtra("listaObservacoes") as ArrayList<Observacao>
            var intent = Intent(this, MapActivity::class.java)
            // enviar para a atividade Mapa a latitude e longitude atuais
            intent.putExtra("latitude", latitude.toString())
            intent.putExtra("longitude", longitude.toString())
            intent.putExtra("option", "OPTION_2")
            intent.putExtra("utilizador", utilizador)
            intent.putExtra("listaObservacoes", listaObservacoes)
            startActivityForResult(intent, PICK_MARKER_CODE)
        }

        // verificação de campos e alterações na imagem para atualizar observação
        binding.btnEditarObs.setOnClickListener {

            especie = binding.txtEspecie.text.toString()
            data = binding.txtData.text.toString()
            descricao = binding.txtDescricao.text.toString()
            var id = idObs

            // verifica se a data está no formato dd/mm/aaaa
            if (validarData(data)) {
                // cria um dialog para mostra o loading enquanto os dados são enviados para as APIs
                val loading = Dialog(this)
                loading.setContentView(R.layout.loading)
                loading.window?.setLayout(
                    (resources.displayMetrics.widthPixels).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                loading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading.setCancelable(false)

                // usa o Glide para mostra o GIF com movimento
                val loadingImageView = loading.findViewById<ImageView>(R.id.loadingImageView)
                Glide.with(this)
                    .load(R.drawable.loading)
                    .into(loadingImageView)
                loading.show()


                if (especie == "") {
                    Toast.makeText(
                        applicationContext,
                        R.string.n_o_foi_especificada_a_esp_cie,
                        Toast.LENGTH_SHORT
                    ).show()
                    loading.dismiss()
                } else if (data == "") {
                    Toast.makeText(
                        applicationContext,
                        R.string.n_o_foi_especificada_a_data,
                        Toast.LENGTH_SHORT
                    ).show()
                    loading.dismiss()
                } else {
                    //envia fotografia para o ImgBB e guarda o displayURL que vai ser retornado
                    if (!checkNullBitMap()) {
                        EnvioFotografia.enviarFoto(imgBitmap, applicationContext, object :
                            EnvioFotografiaCallback {
                            override fun onSucess(url: String) {
                                foto = url
                                editarObs(
                                    id,
                                    utilizador,
                                    latitude,
                                    longitude,
                                    foto,
                                    descricao,
                                    data,
                                    especie,
                                    loading
                                )
                            }

                            override fun onError(mensagemErro: String) {
                                Log.e(
                                    "EditarObsActivity",
                                    "Erro ao obter o displayURL: $mensagemErro"
                                )
                                Toast.makeText(
                                    applicationContext,
                                    "Erro ao atualizar Observação.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loading.dismiss()
                            }

                        })
                    } else {
                        editarObs(
                            id,
                            utilizador,
                            latitude,
                            longitude,
                            foto,
                            descricao,
                            data,
                            especie,
                            loading
                        )
                    }

                }
            }
        }
    }

    /*
    * verifica se a imagem não foi alterada e é a original
     */
    private fun checkNullBitMap(): Boolean{
        try{
            if (imgBitmap != null)
                return false
        } catch (e: Exception){
            return true
        }
        return false
    }

     /*
     * valida se a data está no formato inserid dd/MM/yyyy
     */
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

    /*
     * envia um request de atividade para abrir a galeria do utilizador
     */
    private fun abrirGaleria() {
        val galeria = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galeria, PICK_IMAGE_REQUEST)
    }

    /*
     * envia um request activity para capturar foto
     */
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

    /*
     * Verifica a permissão para utilização de câmara para permitir a captura de foto
     */
    private fun verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            tirarFoto()
        }
    }

    /*
    * inicia a atividade correspondente ao request code enviado
    * as atividades são captura de foto e obtenção
     */
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

    /*
    * atualiza a imagem consoante o request code enviado
    * distingue se foto veio da galeria ou se veio da captura de foto
     */
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

    /*
    * conversão da imagem uri para bitmap
     */
    private fun converterParaBitmap(uriImagem: Uri): Bitmap? {
        try {
            val inputStream = contentResolver.openInputStream(uriImagem)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    /*
     * chamada de post da observação à API Sheety
     */
    private fun editarObs(id: String, utilizador: String, lat: Double, long: Double, foto: String, descricao: String, data: String, especie: String, loading: Dialog){
        // cria o objeto Observacao
        val novaObservacao = Observacao(id = id, utilizador = utilizador, lat = lat.toString(), long = long.toString(), foto = foto, descricao = descricao, data = data, especie = especie)

        // encapsula dentro de um objecto Observacao para construir corretamente o JSON a enviar
        val putObservacao = ObservacaoPUT(observacao = novaObservacao)
        Log.d("EditarObsActivity", "JSON enviado: ${Gson().toJson(putObservacao)}")

        val call = servicoAPI.editarObservacao(id, putObservacao)
        call.enqueue(object : Callback<RespostaAPI> {
            override fun onResponse(call: Call<RespostaAPI>, response: Response<RespostaAPI>) {
                if (response.isSuccessful) {
                    Log.d("EditarObsActivity", "onSucessful: ${response.body()}")
                    Toast.makeText(
                        applicationContext,
                        "Observação alterada com sucesso.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    var intent = Intent(this@EditarObsActivity, MainActivity::class.java)
                    // envia para a atividade Main o utilizador autenticado
                    intent.putExtra("utilizador", utilizador)
                    startActivity(intent)
                } else {
                    Log.e("EditarObsActivity", "Erro: ${response.code()}")
                    Toast.makeText(
                        applicationContext,
                        "Erro. Código: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                loading.dismiss()
            }

            override fun onFailure(call: Call<RespostaAPI>, t: Throwable) {
                Log.e("EditarObsActivity", "Erro: ${t.message}")
                loading.dismiss()
            }
        })

    }

    // volta à MainActivity ao retroceder
    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@EditarObsActivity, MainActivity::class.java)
        intent.putExtra("utilizador", utilizador)
        startActivity(intent)
    }

}