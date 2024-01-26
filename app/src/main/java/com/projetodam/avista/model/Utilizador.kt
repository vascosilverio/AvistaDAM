package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

data class Utilizador(
    @SerializedName("id")
    val id: String?,
    @SerializedName("utilizador")
    val userId: String?,
    @SerializedName("password")
    val password: String?
)