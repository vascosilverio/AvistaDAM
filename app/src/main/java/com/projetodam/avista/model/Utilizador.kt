package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados do Utilizador para comunicar com a API Sheety
 */
data class Utilizador(
    @SerializedName("id")
    val id: String?,
    @SerializedName("utilizador")
    val userId: String?,
    @SerializedName("password")
    val password: String?
)