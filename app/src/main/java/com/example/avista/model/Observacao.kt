package com.example.avista.model

import com.google.gson.annotations.SerializedName

data class Observacao(
    @SerializedName("id")
    val id: String?,
    @SerializedName("utilizador")
    val utilizador: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("long")
    val long: String?,
    @SerializedName("foto")
    val foto: String?,
    @SerializedName("descricao")
    val descricao: String?,
    @SerializedName("data")
    val data: String?,
    @SerializedName("especie")
    val especie: String?
)