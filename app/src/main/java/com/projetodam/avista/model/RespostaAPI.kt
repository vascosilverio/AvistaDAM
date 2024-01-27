package com.projetodam.avista.model

import com.google.gson.annotations.SerializedName

/*
* Modelo de dados da resposta da API Sheety
 */
data class RespostaAPI (
    @SerializedName("code") val code: String?,
    @SerializedName("description") val description: String?
)