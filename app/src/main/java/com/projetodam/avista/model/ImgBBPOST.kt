package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados com os dados a enviar para a API do ImgBB.
* o dado "data" é uma referência para outro modelo de dados abaixo (ImgBBData)
 */
data class ImgBBPOST(
    val data: ImgBBData,
    val success: Boolean,
    val status: Int
)
/*
* Modelo de dados do ImgBBData referido acima para enviar a imagem para a API do ImgBB, se o estado do envio foi sucesso, e o código do estado.
* o dado "data" é uma referência para outro modelo de dados abaixo (ImgBBData)
* realça-se o displayURL que é o URL retornado após o envio da imagem para o acesso direto por HTTP ao JPG para o guardar posteriormente no Sheety
 */
data class ImgBBData(
    val id: String,
    val title: String,
    @SerializedName("url_viewer")
    val urlViewer: String,
    val url: String,
    @SerializedName("display_url")
    val displayUrl: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val time: Long,
    val expiration: Int,
    val image: ImgBBImage,
    val thumb: ImgBBImage,
    @SerializedName("delete_url")
    val deleteUrl: String
)

/*
* Modelo de dados ImgBBImage referenciado no anterior modelo.
 */
data class ImgBBImage(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)
